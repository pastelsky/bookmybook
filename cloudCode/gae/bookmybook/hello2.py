import webapp2
from lxml import html
import time
from google.appengine.ext import ndb
from google.appengine.api import urlfetch
import urllib2
import re

class MainPage(webapp2.RequestHandler):
    def get(self):
        urlfetch.set_default_fetch_deadline(45)
        isbn = self.request.get("isbn")

        bookQuery = BookItem.get_by_id(isbn)

        if bookQuery is None:
            print "Book not in datastore, commencing url fetch"
            result = urllib2.urlopen("http://www.flipkart.com/search&q="+isbn).read()
            tree = html.fromstring(result)

            resultBook = BookItem()
            resultBook.book_name = ''.join(tree.xpath('//h1[contains(concat(" ", normalize-space(@class), " "), " title ") and @itemprop="name"][1]/text()')).strip(' \t\n\r')
            resultBook.book_authors = ''.join(tree.xpath('//div[contains(concat(" ", normalize-space(@class), " "), " bookDetails ")]//strong[contains(., \'Author\')]/following-sibling::a[1]/text()')).strip(' \t\n\r')
            resultBook.book_cover_URL = ''.join(tree.xpath('//div[contains(concat(" ", normalize-space(@class), " "), " mainImage ")]//img[contains(concat(" ", normalize-space(@class), " "), " productImage ")][1]/@data-src')).strip(' \t\n\r')
            resultBook.book_ISBN_13 = isbn.strip(' \t\n\r')
            resultBook.book_publish_year = ''.join(tree.xpath('//table[@class="specTable"]//td[contains(., "Year")]//following-sibling::td[1]//text()')).strip(' \t\n\r')
            if resultBook.book_publish_year != '':
                resultBook.book_publish_year = re.search('(\d{4})', resultBook.book_publish_year).group(0)

            resultBook.book_new_edition_URL = ''.join(tree.xpath('//div[contains(concat(" ", normalize-space(@class), " "), " newerVersion ")]//a[1]/@href')).strip(' \t\n\r')

            if(resultBook.book_new_edition_URL.strip(' ') == ""):
                    resultBook.book_new_edition_URL = ""
            else:
                resultBook.book_new_edition_URL = 'http:www.flipkart.com' + resultBook.book_new_edition_URL
            resultBook.book_language = ''.join(tree.xpath('//div[contains(concat(" ", normalize-space(@class), " "), " bookDetails ")]//div[strong[contains(., \'Language\')]]//text()[not(ancestor::strong)]')).strip(' \t\n\r')
            resultBook.book_cat_level_1 = ''.join(tree.xpath('//div[contains(concat(" ", normalize-space(@class), " "), " breadcrumb-wrap ")]//ul//li[position()=3]//a/text()')).strip(' \t\n\r')
            resultBook.book_cat_level_2 = ''.join(tree.xpath('//div[contains(concat(" ", normalize-space(@class), " "), " breadcrumb-wrap ")]//ul//li[position()=4]//a/text()')).strip(' \t\n\r')                
            resultBook.book_cat_level_3 = ''.join(tree.xpath('//div[contains(concat(" ", normalize-space(@class), " "), " breadcrumb-wrap ")]//ul//li[position()=5]//a/text()')).strip(' \t\n\r')
            resultBook.book_cat_level_4 = ''.join(tree.xpath('//div[contains(concat(" ", normalize-space(@class), " "), " breadcrumb-wrap ")]//ul//li[position()=6]//a/text()')).strip(' \t\n\r')
            resultBook.book_flipkart_price = ''.join(tree.xpath('//div[contains(concat(" ", normalize-space(@class), " "), " shop-section ")]//meta[@itemprop="price"][1]/@content')).strip(' \t\n\r')
            resultBook.book_mrp = ''.join(tree.xpath('//div[contains(concat(" ", normalize-space(@class), " "), " shop-section ")]//span[contains(concat(" ", normalize-space(@class), " "), " price ")]/text()')).strip(' \t\n\r')
            resultBook.book_mrp = re.search(r'\d+', resultBook.book_mrp).group()
            # resultBook.book_mrp = re.search('(\d*)', resultBook.book_mrp).group(0)
            resultBook.key = ndb.Key('BookItem', isbn)
            if(resultBook.isValid()):
                print "Storing it in DB....."
                resultBook.put()
        else:
            resultBook = bookQuery

        expires = time.time() + 130000
        self.response.headers["Expires"] = time.strftime("%a, %d-%b-%Y %T GMT", time.gmtime(expires))
        self.response.headers["Content-Type"] = "application/json"
        self.response.headers["Cache-Control: max-age"] = '130000'
        self.response.headers["Cache-Control"] = "public"

        if(resultBook.isValid()):
            self.response.write(resultBook)


class BookItem(ndb.Model):

    book_name = ndb.StringProperty()
    book_authors = ndb.StringProperty()
    book_cover_URL = ndb.StringProperty()
    book_ISBN_13 = ndb.StringProperty()
    book_publish_year = ndb.StringProperty()
    book_new_edition_URL = ndb.StringProperty()
    book_language = ndb.StringProperty()

    book_cat_level_1 = ndb.StringProperty()
    book_cat_level_2 = ndb.StringProperty()
    book_cat_level_3 = ndb.StringProperty()
    book_cat_level_4 = ndb.StringProperty()

    book_mrp = ndb.StringProperty()
    book_flipkart_price = ndb.StringProperty()
    book_amazon_price = ndb.StringProperty()

    def __init__(self):
        super(BookItem, self).__init__()

    def __repr__(self):
        return '{"pub_year":"%s", "isbn":"%s", "language":"%s", "name":"%s", "author":"%s", "flipkart_price":"%s", "mrp" : "%s", "new_edition_url":"%s", "cover_url":"%s", "cat_level_1":"%s", "cat_level_2":"%s", "cat_level_3":"%s", "cat_level_4":"%s"}' % (self.book_publish_year, self.book_ISBN_13, self.book_language, self.book_name, self.book_authors, self.book_flipkart_price, self.book_mrp,self.book_new_edition_URL, self.book_cover_URL, self.book_cat_level_1, self.book_cat_level_2, self.book_cat_level_3, self.book_cat_level_4)
        
    def isValid(self):
        return self.book_name != '' and self.book_authors != '' and self.book_flipkart_price != ''


app = webapp2.WSGIApplication([
    ('/q2', MainPage),
], debug=True)