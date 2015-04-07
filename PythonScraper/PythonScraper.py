from selenium import webdriver
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as ec
from selenium.webdriver.common.by import By

#Navigate to Main page
firefox = webdriver.Firefox()
firefox.get('http://bettercloud.com')

#Makes sure Main page loads successfully
try:
    WebDriverWait(firefox, 10).until(ec.presence_of_element_located((By.ID, "menu-item-4286")))
    print "Loaded Page: '%s'" % firefox.title
except:
    print "Error Loading"
    firefox.close()

#Hover
element_to_hover_over = firefox.find_element_by_id("menu-item-4286")
hover = ActionChains(firefox).move_to_element(element_to_hover_over)
hover.perform()

#Move to management link and click it
firefox.find_element_by_partial_link_text("Management").click()

#Makes sure Management page loads successfully
try:
    WebDriverWait(firefox, 10).until(ec.presence_of_element_located((By.ID, "content")))
    print "Loaded Page: '%s'" % firefox.title
except:
    print "Error Loading"
    firefox.close()

endings = set('.?!')
lastwords = []

#Iterate through all parts of bio
for i in range(1, 4):
    xpath = '//*[@id="post-4415"]/div/div[2]/div/div[2]/div[2]/p[%d]' % i
    result = firefox.find_element_by_xpath(xpath).text

    #read line and put any word containing an 'endings' char into an array
    for word in result.split(' '):
         if endings & set(word):
             word = word.strip()
             word = word[:-1]
             lastwords.append(word)

#sort using built in sort function
lastwords.sort(key = lambda k : k.lower())

#Encode to ascii or else the elements will print with a unicode identifier
print "\nLast words of bio in alphabetical order:"
print map(lambda j: j.encode('ascii'), lastwords)

#We got what we came for, shut it down
firefox.close()