package shopDate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ShopDates {
	public static void main(String[] args) throws MalformedURLException, IOException {
		//element为单数，getElementById
		//elements为复数 获取的是一组元素或一组数组
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= 5; i++) {
			//获取请求地址 https://tabelog.com/rstLst/chinese i代表第几个页面（5为只取前5个页面）
			String url = "https://tabelog.com/rstLst/chinese/" + i + "/";
			//		String url = "https://tabelog.com/rstLst/chinese/1/";
			//解析网页 jsoup返回Document就是浏览器Document对象
			Document document = Jsoup.parse(new URL(url), 30000);
			Elements elements = document.getElementsByClass("list-rst__rst-data");
			//		Elements elements = document.getElementsByClass("list-rst__rst-name-target cpy-rst-name");

			for (Element el : elements) {
				//取出每个店铺的指定的url 在a标签中
				String url2 = el.getElementsByClass("list-rst__rst-name-target cpy-rst-name").eq(0).attr("href");
				//			String url3="https://tabelog.com/tokyo/A1311/A131101/13210767/";
				Document document1 = Jsoup.parse(new URL(url2), 30000);
				//要一层一层往下扒从大的div（所需要的模块div）往小的div扒直到扒到没有div的时候，eq里面是index
				Element element1 = document1.getElementById("rst-data-head");
				Elements elements1 = element1.getElementsByClass("c-table c-table--form rstinfo-table__table").eq(0);
				for (Element ele : elements1) {
					String name = ele.getElementsByTag("td").eq(0).text();
					String tel = ele.getElementsByTag("td").eq(2).text();
					String address = ele.getElementsByTag("td").eq(4).eq(0).toggleClass("listlink").text();
					list.add(name + "," + tel + "," + address.substring(0, address.lastIndexOf("大きな地図を見る 周辺のお店を探す")));
				}
			}
		}
		//		System.out.println(list);
		ShopDB db = new ShopDB();
		try {
			db.getDBcon();
			for (String str : list) {
				String[] strs = str.split(",");
				Shop shop = new Shop();
				shop.setName(strs[0]);
				shop.setTel(strs[1]);
				shop.setAddress(strs[2]);
				db.insertShopDate(shop);
			}
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.closeDb();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
