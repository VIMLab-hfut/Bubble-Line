# encoding:utf-8
# FileName: craw_lianjia_house
# Date:     2021/06/10 22:52
# Author:   xiaoyi | 小一
# wechat:   zhiqiuxiaoyi
# 公众号：   小一的学习笔记
# email:    zhiqiuxiaoyi@qq.com
# Description: 爬取链接网的二手房信息
import os
import random
import re
import time
from collections import OrderedDict
from datetime import datetime

import pandas as pd
import requests
from bs4 import BeautifulSoup


def get_ua():
    """
    在UA库中随机选择一个UA
    :return: 返回一个库中的随机UA
    """
    ua_list = [
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 OPR/26.0.1656.60",
        "Opera/8.0 (Windows NT 5.1; U; en)",
        "Mozilla/5.0 (Windows NT 5.1; U; en; rv:1.8.1) Gecko/20061208 Firefox/2.0.0 Opera 9.50",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; en) Opera 9.50",
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0",
        "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
        "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.133 Safari/534.16",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.11 TaoBrowser/2.0 Safari/536.11",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER",
        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; LBBROWSER)",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E; LBBROWSER)",
        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
        "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 SE 2.X MetaSr 1.0",
        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SV1; QQDownload 732; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0)",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Maxthon/4.4.3.4000 Chrome/30.0.1599.101 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 UBrowser/4.0.3214.0 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36",
        "Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13",
        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
        "Mozilla/5.0 (Macintosh; U; IntelMac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1Safari/534.50",
        "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1",
        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0",
        "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6",
        "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1090.0 Safari/536.6",
        "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/19.77.34.5 Safari/537.1",
        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
        "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.36 Safari/536.5",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3",
        "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_0) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3",
        "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3",
        "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3",
        "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3",
        "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.0 Safari/536.3",
        "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24"]

    return random.choice(ua_list)


class LianJiaHouse:
    def __init__(self, city, url, page_size, save_file_path, keyword):
        """
        初始化
        @param url: 主起始页
        @param page_size: 每一页的出租房屋个数
        """

        # 搜索关键字
        self.keyword = keyword
        # 城市
        self.city = city
        # 主起始页
        self.base_url = url
        # 当前筛选条件下的页面
        self.current_url = url
        # 行政区域
        self.area = []
        # 户型：一室、二室、三室、四室、五室、五室+
        self.rooms_number = ['l1', 'l2', 'l3', 'l4', 'l5', 'l6']
        # 朝向：朝东+朝南+朝西+朝北+南北
        self.orientation = ['f1', 'f2', 'f3', 'f4', 'f5']
        # 起始页码默认为0
        self.start_page = 0
        # 当前条件下的总数据页数
        self.pages = 0
        # 每一页的出租房屋个数，默认page_szie=30
        self.page_size = page_size
        # 最大页数
        self.max_pages = 100
        # 设置最大数据量，测试用
        self.count = 0
        # 本地文件保存地址
        self.save_file_path = save_file_path
        # 所有已经保存的房屋 id，用来验证去重
        self.house_id = self.get_exists_house_id()
        # 保存数据
        self.data_info = []
        # 系统等待时间：最大时间 + 最小时间（单位：秒）
        self.await_max_time = 5
        self.await_min_time = 3
        # 重连次数
        self.retry = 5
        # 设置爬虫头部，建议多设置一些，防止被封
        self.headers = {
            'User-Agent': get_ua(),
        }

    def get_main_page(self):
        """
        进入主起始页
        @return:
        """
        self.current_url = self.base_url + "rs" + self.keyword + "/"
        # 获取当前筛选条件下数据总条数
        soup, count_main = self.get_house_count()
        # 如果当前当前筛选条件下的数据个数大于最大可查询个数，则设置第一次查询条件
        if int(count_main) > self.page_size*self.max_pages:
            # 获取当前地市的所有行政区域，当做第一个查询条件
            soup_uls = soup.find('div', attrs={'data-role': 'ershoufang'}).div.find_all('a')
            self.area = self.get_area_list(soup_uls)
            # 遍历行政区域，重新生成筛选条件
            for area in self.area:
                self.get_area_page(area)
        else:
            # 直接获取数据
            self.get_pages(int(count_main), '', '', '')

        # 保存数据到本地
        self.data_to_csv()

    def get_area_page(self, area):
        """
        当前搜索条件：行政区域
        @param area:
        @return:
        """
        # 重新拼接行政区域访问的 url
        self.current_url = self.base_url + area + '/' + 'rs' + self.keyword + '/'
        # 获取当前筛选条件下数据总条数
        soup, count_area = self.get_house_count()

        '''如果当前当前筛选条件下的数据个数大于最大可查询个数，则设置第二次查询条件'''
        if int(count_area) > self.page_size * self.max_pages:
            # 遍历居室数量，重新生成筛选条件
            for rooms_number in self.rooms_number:
                self.get_area_and_room_page(area, rooms_number)
        else:
            print('当前筛选条件：{0}， 共 {1} 条数据，正在获取第 {2} 页'.format(area, count_area, self.pages))
            self.get_pages(int(count_area), area, '', '')

    def get_area_and_room_page(self, area, room_number):
        """
        当前搜索条件：行政区域 + 居室数
        @param area: 行政区域
        @param room_number: 居室数
        @return:
        """
        # 重新拼接行政区域 + 居室数访问的 url
        self.current_url = self.base_url + area + '/' + room_number + 'rs' + self.keyword + '/'
        # 获取当前筛选条件下数据总条数
        soup, count_area_rental = self.get_house_count()

        '''如果当前当前筛选条件下的数据个数大于最大可查询个数，则设置第三次查询条件'''
        if int(count_area_rental) > self.page_size * self.max_pages:
            # 遍历朝向，重新生成筛选条件
            for orientation in self.orientation:
                self.get_area_and_room_and_orientation_page(area, room_number, orientation)
        else:
            print('当前搜索条件：{0} {1}， 共 {2} 条数据，正在获取第 {3} 页'.format(
                area, room_number, count_area_rental, self.pages))
            self.get_pages(int(count_area_rental), area, room_number, '')

    def get_area_and_room_and_orientation_page(self, area, room_number, orientation):
        """
        当前搜索条件：行政区域 + 居室数
        @param area: 行政区域
        @param room_number: 居室数
        @param orientation: 朝向
        @return:
        """
        # 重新拼接行政区域 + 居室 + 朝向 访问的 url
        self.current_url = self.base_url + area + '/' + orientation + room_number + 'rs' + self.keyword + '/'
        # 获取当前筛选条件下数据总条数
        soup, count_area_room_orientation = self.get_house_count()

        '''如果当前当前筛选条件下的数据个数大于最大可查询个数，则设置第三次查询条件'''
        if int(count_area_room_orientation) > self.page_size * self.max_pages:
            print('==================无法获取所有数据，当前筛选条件数据个数超过总数，将爬取前100页数据')
            print('当前搜索条件：{0} {1} {2}， 共 {3} 条数据，正在获取第 {4} 页'.format(
                area, room_number, orientation, count_area_room_orientation, self.pages))
            self.get_pages(int(self.page_size * self.max_pages), area, room_number, orientation)

        else:
            print('当前搜索条件：{0} {1} {2}， 共 {3} 条数据，正在获取第 {4} 页'.format(
                area, room_number, orientation, count_area_room_orientation, self.pages))
            self.get_pages(int(count_area_room_orientation), area, room_number, orientation)

    def get_pages(self, count_number, area, rental_method, room_number):
        """
        根据查询到的页面总数据，确定分页
        @param count_number: 总数据量
        @param area: 区域
        @param rental_method: 出租方式
        @param room_number:居室数
        @return:
        """
        # 确定页数
        self.pages = int(count_number/self.page_size) \
            if (count_number%self.page_size) == 0 else int(count_number/self.page_size)+1

        '''遍历每一页'''
        for page_index in range(1, self.pages+1):
            self.current_url = self.base_url + area + '/' + 'pg' + str(page_index) + rental_method + room_number + 'rs' + self.keyword +  '/'
            # 解析当前页的房屋信息，获取到每一个房屋的详细链接
            self.get_per_house()
            page_index += 1
            # 休眠 2 秒
            time.sleep(2)

    def get_per_house(self):
        """
        解析每一页中的每一个房屋的详细链接
        @return:
        """
        # 爬取当前页码的数据
        response = requests.get(url=self.current_url, headers=self.headers, timeout=10)
        soup = BeautifulSoup(response.text, 'html.parser')
        print(self.current_url)
        # ul 目录前面的同一级目录是 div，通过此方式进行定位，可避免最后一页的 ul 出现变动的问题
        soup_ul_list = soup.find('div', class_='bigImgList', attrs={'log-mod': 'list'}).next_sibling
        # soup_ul_list = soup.find('ul', class_='sellListContent', attrs={'log-mod': 'list'})
        # 遍历获取每一个 li 的房屋详情链接和房屋地址
        for soup_li in soup_ul_list.find_all("li"):
            # 定位并获取每一个房屋的详情链接
            detail_href = soup_li.a.get('href')
            # 获取详细链接的编号作为房屋唯一id
            house_id = detail_href.split('/')[-1].replace('.html', '')
            # 如果当前房屋信息已经爬取过
            if self.check_exist(house_id):
                print('房屋id：{0} 已经保存，不再重复爬取！'.format(house_id))
            else:
                # 解析当前房屋的详细数据
                self.get_house_content(detail_href, house_id)

        return ""

    def get_house_content(self, href, house_id):
        """
        获取房屋详细信息页面的内容
        @param href: 详细页面链接
        @param house_id: 上个页面传递的房租id
        @return:
        """
        # 生成一个有序字典，保存房屋结果
        house_info = OrderedDict()
        for i in range(0, self.retry):
            try:
                # 随机休眠3-8 秒
                time.sleep(random.randint(self.await_min_time, self.await_max_time))
                '''爬取页面，获得详细数据'''
                response = requests.get(url=href, headers=self.headers, timeout=10)
                soup = BeautifulSoup(response.text, 'html.parser')
                """解析数据"""
                # 标题数据
                soup_aroundInfo = soup.find('div', class_='aroundInfo')
                house_info['house_address'] = soup_aroundInfo.find('div', class_='communityName').a.text
                print(self.current_url, house_id, house_info['house_address'])

                soup_area = soup_aroundInfo.find('div', class_='areaName').find('span', class_='info')
                house_info['house_region'] = soup_area.find_all('a')[0].text
                house_info['house_area'] = soup_area.find_all('a')[1].text
                house_info['house_id'] = house_id
                # 价格数据
                soup_priceBox = soup.find('div', class_='priceBox')
                if soup_priceBox != None:
                    soup_price = soup_priceBox.find_all("p")
                    house_info['total_price'] = soup_price[0].span.get_text()
                    house_info['unit_price'] = soup_price[1].span.get_text()
                else:
                    house_info['total_price'] = soup.find('span', class_='total').get_text()
                    house_info['unit_price'] = soup.find('span', class_='unitPriceValue').get_text()
                # 基本信息
                soup_base_info = soup.find('div', class_='base').find('div', class_='content').ul.find_all("li")
                house_info['house_layout'] = soup_base_info[0].get_text()
                house_info['house_floor'] = soup_base_info[1].get_text()
                house_info['house_rental_area'] = soup_base_info[2].get_text()
                # 区分别墅的基本信息
                if len(soup_base_info) > 10:
                    house_info['house_structure'] = soup_base_info[3].get_text()
                    house_info['house_inner_area'] = soup_base_info[4].get_text()
                    house_info['house_building_type'] = soup_base_info[5].get_text()
                    house_info['house_orientation'] = soup_base_info[6].get_text()
                    house_info['house_building_structure'] = soup_base_info[7].get_text()
                    house_info['house_decoration'] = soup_base_info[8].get_text()
                    house_info['house_elevator_sytle'] = soup_base_info[9].get_text()
                    house_info['house_elevator'] = soup_base_info[10].get_text()
                else:
                    house_info['house_inner_area'] = soup_base_info[3].get_text()
                    house_info['house_orientation'] = soup_base_info[4].get_text()
                    house_info['house_building_structure'] = soup_base_info[5].get_text()
                    house_info['house_decoration'] = soup_base_info[6].get_text()
                    house_info['house_building_type'] = soup_base_info[7].get_text()
                    house_info['house_structure'] = ""
                    house_info['house_elevator_sytle'] = ""
                    house_info['house_elevator'] = ""

                # 交易属性
                soup_transaction_info = soup.find('div', class_='transaction').find('div', class_='content').ul.find_all("li")
                house_info['house_listing_time'] = soup_transaction_info[0].find_all('span')[1].text
                house_info['house_transaction_type'] = soup_transaction_info[1].find_all('span')[1].text
                house_info['house_last_time'] = soup_transaction_info[2].find_all('span')[1].text
                house_info['house_useage'] = soup_transaction_info[3].find_all('span')[1].text
                house_info['house_years'] = soup_transaction_info[4].find_all('span')[1].text
                house_info['house_property'] = soup_transaction_info[5].find_all('span')[1].text
                house_info['house_mortgage_info'] = soup_transaction_info[6].find_all('span')[1]['title']
                house_info['house_book'] = soup_transaction_info[7].find_all('span')[1].text
                if len(soup_transaction_info) > 8:
                    house_info['house_fx_id'] = soup_transaction_info[8].find_all('span')[1].text
                else:
                    house_info['house_fx_id'] = ''

                '''解析经纬度数据'''
                location_str = response.text[re.search(r"resblockPosition:", response.text).span()[0]:
                                             re.search(r"cityId:", response.text).span()[0]]
                location_arr = location_str.split("'")[1].split(",")
                house_info['house_longitude'] = location_arr[0]
                house_info['house_latitude'] = location_arr[1]
                house_info['city'] = self.city

                # 保存当前信息
                self.data_info.append(house_info)
                self.count += 1

                '''超过10条数据，保存到本地'''
                if len(self.data_info) >= 10:
                    self.data_to_csv()
                # 处理无异常在，跳出循环，否则，进行重试
                break
            except Exception as e:
                print(e)
                print('爬取数据异常，重新尝试爬取数据。爬取地址：{0}'.format(href))

    def check_exist(self, house_id):
        """
        检查当前要获取的房屋数据是否已经存在
        @param house_id:
        @return:
        """
        # 通过检查当前数据中 房屋id 是否存在
        if house_id in self.house_id:
            return True
        else:
            self.house_id.append(house_id)
            return False

    def get_exists_house_id(self):
        """
        通过已经爬取到的房屋信息，并获取房屋id
        @return:
        """
        if os.path.exists(self.save_file_path):
            df_data = pd.read_csv(self.save_file_path, encoding='utf-8', )
            df_data['house_id'] = df_data['house_id'].astype(str)
            return df_data['house_id'].to_list()
        else:
            return []

    def data_to_csv(self):
        """
        保存/追加数据到本地
        @return:
        """
        # 获取数据并保存成 DataFrame
        df_data = pd.DataFrame(self.data_info)

        if os.path.exists(self.save_file_path) and os.path.getsize(self.save_file_path):
            # 追加写入文件
            df_data.to_csv(self.save_file_path, mode='a', encoding='utf-8', header=False, index=False)
        else:
            # 写入文件，带表头
            df_data.to_csv(self.save_file_path, mode='a', encoding='utf-8', index=False)

        print("save success!")
        # 清空当前 数据集
        self.data_info = []

    def get_house_count(self):
        """
        获取当前筛选条件下的房屋数据个数
        @param text:
        @return:
        """
        # 爬取区域起始页面的数据
        print("hhhhh")
        print(self.current_url)
        response = requests.get(url=self.current_url, headers=self.headers)
        # 通过 BeautifulSoup 进行页面解析
        soup = BeautifulSoup(response.text, 'html.parser')
        # 获取数据总条数
        count = soup.find('h2', class_='total fl').find('span').string.lstrip()

        return soup, count

    def get_area_list(self, soup_uls):
        """
        获取地市的所有行政区域信息，并保存
        @param soup_uls:
        @return:
        """
        area_list = []
        for soup_ul in soup_uls:
            # 获取 ul 中的 a 标签的 href 信息中的区域属性
            href = soup_ul.get('href')
            # 跳过第一条数据
            if href.endswith('/ershoufang/'):
                continue
            else:
                # 获取区域数据，保存到列表中
                area_list.append(href.replace('/ershoufang/', '').replace('/rs' + self.keyword + '/', ''))

        return area_list


if __name__ == '__main__':
    city_number = 'hf'
    city_name = '合肥'
    url = 'https://{0}.lianjia.com/ershoufang/'.format(city_number)
    page_size = 30
    save_file_path = r'二手房数据-%s.csv' %city_name
    lianjia_house = LianJiaHouse(city_name, url, page_size, save_file_path, "地铁")
    lianjia_house.get_main_page()