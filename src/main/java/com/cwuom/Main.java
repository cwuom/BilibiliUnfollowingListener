package com.cwuom;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author cwuom
 * bilibili取关/关注检测器
 * （献给每一个取关的人）
 * ============================
 * bilibili@im-cwuom
 * 跑路交流群：893018099
 * WeChat: cwuomcwuom00
* */



public class Main {
    // =============================================================

    // 你需要变动的地方
    public static String CookiesPath = "cookie.txt"; // cookie存放路径
    public static String UID = "473400804"; // 你的UID，此项确定了你的监听目标
    public static int sleep_time = 20000; // 休眠时间，若持续风控可向上更改此数值。默认20000ms(20s)

    public static boolean FollowingListener = true; // 关注检测提示 [true : 开启提示] [false : 关闭提示]
    public static boolean UnFollowingListener = true; // 取关检测提示 [true : 开启提示] [false : 关闭提示]

    public static String mail = "cwuomcwuom@163.com"; //发送对象的邮箱 (用于接受提示邮件)


    // =============================================================


    // 无需变动
    private static final List<Fan> Fan = new ArrayList<Fan>(); // 缓存粉丝数据
    private static List<Fan> TempFan = new ArrayList<Fan>(); // 缓存取关列表
    private static final List<Fan> NewFans = new ArrayList<Fan>(); // 缓存粉丝数据
    private static int NewFansNum = 0; // 新粉丝数量

    // =============================================================

    /*
    * 自定义粉丝数据结构体
    * */
    private static class Fan{
        String mid; // UID
        int mtime; // 关注日期
        String name; // 名字

        public Fan(String mid, int mtime, String name) {
            this.mid = mid;
            this.mtime = mtime;
            this.name = name;
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        // 读取cookies
        String cookies = ReadCookies();

        // https://api.bilibili.com/x/relation/stat?vmid=473400804
        System.out.println("===============================\nBilibili Unfollowing Listener V2023.4.14\n bilibili @ im-cwuom | 仅供娱乐学习\n===============================");

        boolean iUnfollowing = false; // 是否触发取关操作
        boolean iFollowing = false; // 是否触发关注操作
        boolean state = true;


        System.out.println("[V] 校验cookies... ");
        String jsonV = String.valueOf(GetFans(cookies, 6));
        if (jsonV.contains("限制只访问前5页")){
            System.out.println("[X] 校验失败。你的cookie已过期或不完整，请尝试更新您的cookie (限制只访问前5页)");
            Thread.sleep(1000000);
            System.exit(0);
        }

        System.out.println("[OK] cookie校验通过!");

        int fans_num = GetFansNum(cookies);
        System.out.println("[!] total: "+fans_num);

        while (true) {
            try {
                System.out.println("[+] starting");

                if (state){
                    // 获取最大粉丝页数
                    int max_pn = fans_num / 50;
                    if (fans_num % 50 != 0){
                        max_pn++;
                    }

                    int n = 0; // 记录遍历数量
                    for (int i = 0; i < max_pn; i++){ // 页数遍历
                        String json = String.valueOf(GetFans(cookies, i+1));
                        JSONObject jsonObject = JSONObject.fromObject(JSONObject.fromObject(json).get("data"));
                        System.out.println("[reading] pn="+(i+1));
                        JSONArray fans_list = JSONArray.fromObject(jsonObject.get("list"));


                        for (int x = 0; x < fans_list.size(); x++){ // 指定页数中的每一个粉丝数据遍历
                            JSONObject fan_date = fans_list.getJSONObject(x);
                            // 获取部分粉丝数据
                            String mid = fan_date.getString("mid"); // UID
                            int mtime = 0;
                            try { // 部分用户不会显示mtime，原因不详
                                mtime = fan_date.getInt("mtime"); // 关注日期
                            } catch (Exception ignored) {}
                            String name = fan_date.getString("uname"); // 名字


                            if(!iUnfollowing){ // 无取关操作，直接添加到结构中
                                Fan.add(new Fan(mid, mtime, name));
                                if (NewFansNum != 0){ // 新关注
                                    NewFans.add(new Fan(mid, mtime, name));
                                    NewFansNum--;
                                }
                            }
                            else{ // 触发取关
                                for(int y = 0; y < TempFan.size(); y++){ // 遍历旧粉丝列表
                                    if (Objects.equals(TempFan.get(y).mid, mid)){ // 找到与当前uid相同的用户
                                        TempFan.remove(y); // 抹掉此用户
                                        break;
                                    }
                                }
                            }


                            n++;
                        }
                    }

                    state = false;
                }

                if (iFollowing && FollowingListener){ // 发送关注检测的邮件
                    fans_num = GetFansNum(cookies);
                    System.out.println("[!] 关注列表如下...");
                    System.out.println("size: "+Fan.size());


                    for (Main.Fan fan : NewFans) { // 遍历剩余的用户
                        System.out.println(fan.name + " -- " + "https://space.bilibili.com/" + fan.mid);
                        String title = "关注检测";
                        String content = "<div style=\"max-width: 460px;margin: 64px auto;opacity: 0.87;padding: 24px 32px;box-shadow: 0 2px 2px 0 rgba(0, 0, 0, .14), 0 3px 1px -2px rgba(0, 0, 0, .2), 0 1px 5px 0 rgba(0, 0, 0, .12);border-radius: 2px;background: white\">\n" +
                                "<h2 style=\"margin: 0 0 16px 0\">睿站关注检测</h2>\n" +
                                "<p>你的 <b>bilibili</b> 账户粉丝发生了改变</p>\n" +
                                "<h4 style=\"margin: 0;color: orange\">粉丝数: "+fans_num+"</h4>\n" +
                                "<p>\n" +
                                "    用户UID:"+fan.mid+"\n" +
                                "    <br>\n" +
                                "    用户昵称:"+fan.name+"\n" +
                                "    <br>\n" +
                                "    <a href=\""+"https://space.bilibili.com/" + fan.mid+"\">用户主页链接</a>\n" +
                                "</p>\n" +
                                "</div>\n" +
                                "</div></div>";
                        MailInfo info = new MailInfo();
                        info.setToAddress(mail);
                        info.setSubject(title);
                        info.setContent(content);
                        try {
                            MailSendUtil.sendHtmlMail(info);
                        } catch (Exception e) {
                            System.out.print("'"+title+"'的邮件发送失败！");
                            e.printStackTrace();
                        }
                    }

                    NewFans.clear();
                    iFollowing = false;
                }

                if (iUnfollowing && UnFollowingListener){ // 发送取关检测的邮件
                    fans_num = GetFansNum(cookies);
                    System.out.println("[!] 取关列表如下...");

//                System.out.println(TempFan.size());
                    for (Main.Fan fan : TempFan) { // 遍历剩余的用户
                        Long timestamp = (long)(fan.mtime)*1000; // 转换时间戳
                        String sd = timestampToDateStr(timestamp,DATETIME_CONVENTIONAL_CN);
                        System.out.println(fan.name + " -- " + "https://space.bilibili.com/" + fan.mid + "--" + sd);

                        String title = "取关检测";
                        String content = "<div style=\"max-width: 460px;margin: 64px auto;opacity: 0.87;padding: 24px 32px;box-shadow: 0 2px 2px 0 rgba(0, 0, 0, .14), 0 3px 1px -2px rgba(0, 0, 0, .2), 0 1px 5px 0 rgba(0, 0, 0, .12);border-radius: 2px;background: white\">\n" +
                                "<h2 style=\"margin: 0 0 16px 0\">睿站取关检测</h2>\n" +
                                "<p>你的 <b>bilibili</b> 账户粉丝发生了改变</p>\n" +
                                "<h4 style=\"margin: 0;color: orange\">粉丝数: "+fans_num+"</h4>\n" +
                                "<p>\n" +
                                "    用户UID:"+fan.mid+"<br>关注时间: "+sd+"\n" +
                                "    <br>\n" +
                                "    用户昵称:"+fan.name+"\n" +
                                "    <br>\n" +
                                "    <a href=\""+"https://space.bilibili.com/" + fan.mid+"\">用户主页链接</a>\n" +
                                "</p>\n" +
                                "</div>\n" +
                                "</div></div>";
                        MailInfo info = new MailInfo();
                        info.setToAddress(mail);
                        info.setSubject(title);
                        info.setContent(content);
                        try {
                            MailSendUtil.sendHtmlMail(info);
                        } catch (Exception e) {
                            System.out.print("'"+title+"'的邮件发送失败！");
                            e.printStackTrace();
                        }
                    }

                    Fan.clear();
                    TempFan.clear();
                    iUnfollowing = false;
                }

                int fans_num2 = GetFansNum(cookies);

                if (fans_num2 < fans_num){
                    System.out.println("[/] 检测到取关操作，正在查找此用户...");
                    TempFan = Fan;
                    state = true;
                    System.out.println("size: "+TempFan.size());
                    iUnfollowing = true;
                } else if (fans_num2 > fans_num) {
                    NewFansNum = fans_num2 - fans_num;
                    System.out.println("[/] 新增了"+NewFansNum+"个粉丝!");
                    Fan.clear();
                    iFollowing = true;
                    state = true;
                }

                fans_num = GetFansNum(cookies);

                System.out.println("[=] wait 20s");
                System.out.println("size: " + Fan.size());
                Thread.sleep(sleep_time);

            } catch (Exception e) { // 被风控了
                jsonV = String.valueOf(GetFans(cookies, 6));
                if (jsonV.contains("限制只访问前5页")){
                    System.out.println("[X] cookie已经失效...");
                    String title = "cookie检测";
                    String content = "<div style=\"max-width: 460px;margin: 64px auto;opacity: 0.87;padding: 24px 32px;box-shadow: 0 2px 2px 0 rgba(0, 0, 0, .14), 0 3px 1px -2px rgba(0, 0, 0, .2), 0 1px 5px 0 rgba(0, 0, 0, .12);border-radius: 2px;background: white\">\n" +
                            "<h2 style=\"margin: 0 0 16px 0\">您的cookie目前已失效</h2>\n" +
                            "<p>请及时<b>更新</b>您的cookie!</p>\n" +
                            "<h4 style=\"margin: 0;color: orange\">程序将立刻停止运行!</h4>\n" +
                            "<p>\n" +
                            "报错详情\n" + e +
                            "</p>\n" +
                            "</div>\n" +
                            "</div></div>";
                    MailInfo info = new MailInfo();
                    info.setToAddress(mail);
                    info.setSubject(title);
                    info.setContent(content);
                    try {
                        MailSendUtil.sendHtmlMail(info);
                    } catch (Exception e2) {
                        System.out.print("'"+title+"'的邮件发送失败！");
                        e2.printStackTrace();
                    }
                    System.exit(-1);
                }else{
                    String title = "风控检测";
                    String content = "<div style=\"max-width: 460px;margin: 64px auto;opacity: 0.87;padding: 24px 32px;box-shadow: 0 2px 2px 0 rgba(0, 0, 0, .14), 0 3px 1px -2px rgba(0, 0, 0, .2), 0 1px 5px 0 rgba(0, 0, 0, .12);border-radius: 2px;background: white\">\n" +
                            "<h2 style=\"margin: 0 0 16px 0\">睿站风控检测</h2>\n" +
                            "<p>你已被 <b>叔叔</b> 风控！</p>\n" +
                            "<h4 style=\"margin: 0;color: orange\">程序将会自动休眠600s...</h4>\n" +
                            "<p>\n" +
                            "风控报错\n" + e +
                            "</p>\n" +
                            "</div>\n" +
                            "</div></div>";
                    MailInfo info = new MailInfo();
                    info.setToAddress(mail);
                    info.setSubject(title);
                    info.setContent(content);
                    try {
                        MailSendUtil.sendHtmlMail(info);
                    } catch (Exception e2) {
                        System.out.print("'"+title+"'的邮件发送失败！");
                        e2.printStackTrace();
                    }
                    Thread.sleep(1000*600);
                }
            }
        }
    }


    /*
    * 获取粉丝数量
    * */
    public static int GetFansNum(String cookies) throws IOException {
        String json = String.valueOf(GetFans(cookies, 0));
        JSONObject jsonObject = JSONObject.fromObject(JSONObject.fromObject(json).get("data")); // STRING->JSON
        return jsonObject.getInt("total");
    }

    /*
     * 获取粉丝列表
     * */
    public static StringBuilder GetFans(String cookies, int pn) throws IOException {
        String urlPath = "http://api.bilibili.com/x/relation/followers?&vmid="+UID+"&pn=" + pn;
        URL url = new URL(urlPath);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Cookie", cookies);
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.39");
        conn.setDoInput(true);
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb;
    }


    /*
     * 读取cookies
     *  */
    public static String ReadCookies() throws IOException {
        String fileName = CookiesPath;
        Path path = Paths.get(fileName);
        List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
        return allLines.get(0);
    }

    /*
    * 时间戳转换
    * */
    public static final String DATETIME_CONVENTIONAL_CN = "yyyy-MM-dd HH:mm:ss";
    public static String timestampToDateStr(Long timestamp,String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String sd = sdf.format(new Date(timestamp)); // 时间戳转换日期
        return sd;
    }
}
