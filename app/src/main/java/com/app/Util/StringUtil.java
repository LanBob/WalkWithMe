package com.app.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.app.JMS.bean.ChatListBean;
import com.app.JMS.bean.Message;
import com.app.JMS.bean.MsgSendStatus;
import com.app.JMS.bean.MsgType;
import com.app.JMS.bean.SqlMessage;
import com.app.JMS.util.DBHelper;
import com.app.JMS.util.FileUtils;
import com.app.JMS.util.LogUtil;
import com.app.MainApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okio.ByteString;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Created by sendtion on 2016/6/24.
 */
public class StringUtil {

    private static SharedPreferencesHelper helper;

    static {
        helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");
    }

    public static String getValue(String key) {
        return helper.getString(key);
    }

    private static final String chatListKey = "chatListKey";

    /**
     * {
     * "name":登录框的账号
     * "password":登录框的密码
     * "first":是否首次登录
     * "remenberPassword":是否记住密码，取值Y或N
     * <p>
     * "isAlreadyLogin":是否已经登录，取值：Y或N
     * "username":登录的账号，等于手机号
     * "isAlreadySetOwnData":是否设置了个人信息,取值:Y或N
     * "score":评分
     * newCount :用于登录框的记住密码
     * }
     *
     * @param key
     * @param value
     */
    public static void putValue(String key, String value) {
        helper.putValues(new SharedPreferencesHelper.ContentValue(key, value));
    }

    public static void addChatList(String userId) {
        List<String> list = getChatListKeys();
        if (list == null) {
            list.add(MyUrl.getKefu());
        }
        helper.putValues(new SharedPreferencesHelper.ContentValue(chatListKey, userId));
    }

    public static List<String> getChatListKeys() {
        String list = helper.getString(chatListKey);
        List<String> stringList = new ArrayList<>();
        if (list == null) {
            stringList.add(MyUrl.getKefu());
        }
        return stringList;
    }

    public static String millToTime(Long mill) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(mill));
    }

    public static String getToMinute(Long t) {
//        Long t = System.currentTimeMillis();
        String time = millToTime(t);
        return time.substring(5, 15);
    }

    private static SQLiteDatabase db = null;
    private static SQLiteDatabase messageDb = null;


    //    数据库=====================================
    static {
        //依靠DatabaseHelper的构造函数创建数据库
        DBHelper dbHelper = new DBHelper(MainApplication.getContext(), "test_db", null, 1);
        db = dbHelper.getWritableDatabase();
    }

//    create table chatbean(userId varchar(20),count int,time varchar(20))

    public static void insertChatListBean(ChatListBean chatListBean) {
        if (db == null) {
            DBHelper dbHelper = new DBHelper(MainApplication.getContext(), "test_db", null, 1);
            db = dbHelper.getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("userId", chatListBean.getUserId());
        values.put("count", chatListBean.getCount());
        values.put("time", chatListBean.getTime());
        db.delete("chatbean", "userId = ?", new String[]{chatListBean.getUserId()});
        //数据库执行插入命令
        db.insert("chatbean", null, values);
    }

    public static List<ChatListBean> query() {
        if (db == null) {
            DBHelper dbHelper = new DBHelper(MainApplication.getContext(), "test_db", null, 1);
            db = dbHelper.getWritableDatabase();
        }
        LogOutUtil.d("query listBean");
        //创建游标对象
        Cursor cursor = db.query("chatbean", new String[]{"userId", "count", "time"}, null, null, null, null, null);

        //利用游标遍历所有数据对象
        //为了显示全部，把所有对象连接起来，放到TextView中
        List<ChatListBean> listBeans = new ArrayList<>();
        while (cursor.moveToNext()) {
            ChatListBean chatListBean = new ChatListBean();
            chatListBean.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
            chatListBean.setCount(Integer.valueOf(cursor.getString(cursor.getColumnIndex("count"))));
            chatListBean.setTime(cursor.getString(cursor.getColumnIndex("time")));
            listBeans.add(chatListBean);
        }
        return listBeans;
    }

    public static void update(ChatListBean chatListBean) {
        if (db == null) {
            DBHelper dbHelper = new DBHelper(MainApplication.getContext(), "test_db", null, 1);
            db = dbHelper.getWritableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put("userId", chatListBean.getUserId());
        values.put("count", chatListBean.getCount());
        values.put("time", chatListBean.getTime());
        db.update("chatbean", values, "userId = ?", new String[]{chatListBean.getUserId()});
    }

    public static int getUserIdCount(String userId) {
        if (db == null) {
            DBHelper dbHelper = new DBHelper(MainApplication.getContext(), "test_db", null, 1);
            db = dbHelper.getWritableDatabase();
        }
        ChatListBean chatListBean = new ChatListBean();
        Cursor cursor = db.query("chatbean", new String[]{"userId", "count", "time"}, "userId = ?", new String[]{userId}, null, null, null);
        while (cursor.moveToNext()) {
            chatListBean.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
            chatListBean.setCount(Integer.valueOf(cursor.getString(cursor.getColumnIndex("count"))));
            chatListBean.setTime(cursor.getString(cursor.getColumnIndex("time")));
        }
        if (chatListBean != null && chatListBean.getCount() > 0) {
            return chatListBean.getCount();
        } else {
            return 0;
        }
    }

    public static void delete(String userId) {
        if (db == null) {
            DBHelper dbHelper = new DBHelper(MainApplication.getContext(), "test_db", null, 1);
            db = dbHelper.getWritableDatabase();
        }
        db.delete("chatbean", "userId=?", new String[]{userId});
    }

    //    消息内容
    public static List<SqlMessage> getSqlMessage(String userId) {
//        String sql6 = "create table sqlmessage(userId varchar(20),path varchar(20))";
        if (db == null) {
            DBHelper dbHelper = new DBHelper(MainApplication.getContext(), "test_db", null, 1);
            db = dbHelper.getWritableDatabase();
        }
        List<SqlMessage> sqlMessageList = new ArrayList<>();
        Cursor cursor = db.query("sqlmessage", new String[]{"userId", "path", "time"}, "userId = ?", new String[]{userId}, null, null, "time");
        while (cursor.moveToNext()) {
            SqlMessage sqlMessage = new SqlMessage();
            sqlMessage.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
            sqlMessage.setPath(cursor.getString(cursor.getColumnIndex("path")));
            sqlMessage.setTime(cursor.getString(cursor.getColumnIndex("time")));
            sqlMessageList.add(sqlMessage);
        }
        return sqlMessageList;
    }

    public static void insertSqlMessage(SqlMessage sqlMessage) {
        if (db == null) {
            DBHelper dbHelper = new DBHelper(MainApplication.getContext(), "test_db", null, 1);
            db = dbHelper.getWritableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put("userId", sqlMessage.getUserId());
        values.put("path", sqlMessage.getPath());
        values.put("time", sqlMessage.getTime());
        LogOutUtil.d("insert sqlMessage" + sqlMessage.getUserId());
        //数据库执行插入命令
        db.insert("sqlmessage", null, values);
    }


    //    数据库=====================================
    /**
     * 创建文件
     *@param path 文件所在目录的目录名
     * @param fileName 文件名
     * @return 文件新建成功则返回true
     */
    public static boolean createFile(String path, String fileName) {
        File file = new File(path + File.separator + fileName);
        if (file.exists()) {
            return false;
        } else {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 将字符串写入文件
     * @param fileStr 文件的绝对路径
     * @param isAppend true从尾部写入，false从头覆盖写入
     */
    public static void writeFile(byte[] bytes, String fileStr, boolean isAppend) {
        try {
            File file = new File(fileStr);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream f = new FileOutputStream(fileStr, isAppend);
            f.write(bytes);
            f.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static byte[] read(File file){
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            try {
                ios = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }


    public static boolean isBigDecimal(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        int i = (chars[0] == '-') ? 1 : 0;
        if (i == sz) return false;

        if (chars[i] == '.') return false;//除了负号，第一位不能为'小数点'

        boolean radixPoint = false;
        for (; i < sz; i++) {
            if (chars[i] == '.') {
                if (radixPoint) return false;
                radixPoint = true;
            } else if (!(chars[i] >= '0' && chars[i] <= '9')) {
                return false;
            }
        }
        return true;
    }


    /**
     * @param targetStr 要处理的字符串
     * @description 切割字符串，将文本和img标签碎片化，如"ab<img>cd"转换为"ab"、"<img>"、"cd"
     */
    public static List<String> cutStringByImgTag(String targetStr) {
        List<String> splitTextList = new ArrayList<String>();
        Pattern pattern = Pattern.compile("<img.*?src=\\\"(.*?)\\\".*?>");
        Matcher matcher = pattern.matcher(targetStr);
        int lastIndex = 0;
        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                splitTextList.add(targetStr.substring(lastIndex, matcher.start()));
            }
            splitTextList.add(targetStr.substring(matcher.start(), matcher.end()));
            lastIndex = matcher.end();
        }
        if (lastIndex != targetStr.length()) {
            splitTextList.add(targetStr.substring(lastIndex, targetStr.length()));
        }
        return splitTextList;
    }

    /**
     * 获取img标签中的src值
     *
     * @param content
     * @return
     */
    public static String getImgSrc(String content) {
        String str_src = null;
        //目前img标签标示有3种表达式
        //<img alt="" src="1.jpg"/>   <img alt="" src="1.jpg"></img>     <img alt="" src="1.jpg">
        //开始匹配content中的<img />标签
        Pattern p_img = Pattern.compile("<(img|IMG)(.*?)(/>|></img>|>)");
        Matcher m_img = p_img.matcher(content);
        boolean result_img = m_img.find();
        if (result_img) {
            while (result_img) {
                //获取到匹配的<img />标签中的内容
                String str_img = m_img.group(2);

                //开始匹配<img />标签中的src
                Pattern p_src = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                Matcher m_src = p_src.matcher(str_img);
                if (m_src.find()) {
                    str_src = m_src.group(3);
                }
                //结束匹配<img />标签中的src

                //匹配content中是否存在下一个<img />标签，有则继续以上步骤匹配<img />标签中的src
                result_img = m_img.find();
            }
        }
        return str_src;
    }

    /**
     * 关键字高亮显示
     *
     * @param target 需要高亮的关键字
     * @param text   需要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     * SpannableStringBuilder textString = TextUtilTools.highlight(item.getItemName(), KnowledgeActivity.searchKey);
     * vHolder.tv_itemName_search.setText(textString);
     */
    public static SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(Color.parseColor("#EE5C42"));// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 从html文本中提取图片地址，或者文本内容
     *
     * @param html       传入html文本
     * @param isGetImage true获取图片，false获取文本
     * @return
     */
    public static ArrayList<String> getTextFromHtml(String html, boolean isGetImage) {
        ArrayList<String> imageList = new ArrayList<>();
        ArrayList<String> textList = new ArrayList<>();
        //根据img标签分割出图片和字符串
        List<String> list = cutStringByImgTag(html);
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i);
            if (text.contains("<img") && text.contains("src=")) {
                //从img标签中获取图片地址
                String imagePath = getImgSrc(text);
                imageList.add(imagePath);
            } else {
                textList.add(text);
            }
        }
        //判断是获取图片还是文本
        if (isGetImage) {
            return imageList;
        } else {
            return textList;
        }
    }


    public static String toMD5(String str) {
        //定义一个字节数组，接收加密完成的密码
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(str.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        /**
         * public BigInteger(int signum,byte[] magnitude)
         * signum:生成的大数字的符号。-1表示负数，0表示零，1表示正数
         * magnitude：需要转化的字节数组
         */
        String md4code = null;
        String md5code = new BigInteger(1, secretBytes).toString(16);//16进制数字
        //如果生成的数字未满32位，要在前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 传输对象
     */

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Log.e("objectsize", bytes.length + " ");
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object byteToObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            System.out.println("1");
            ois.close();
            bis.close();
            System.out.println("4");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    /**
     * 获得指定文件的byte数组
     */
    public static ByteString getByteString(Object object) {
        byte[] buffer = toByteArray(object);
        return ByteString.of(buffer, 0, buffer.length);
    }

    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * str 输入字符串
     *
     * @param str
     * @return 是否是字符串
     */
    public static boolean isInteger(String str) {
        //加或减出现一次或者零次，然后数字出现任意次
//        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (str == null || "".equals(str))
            return false;
        Pattern pattern = Pattern.compile("^[\\d]*$");
        return pattern.matcher(str).matches();
    }


    private Message getBaseSendMessage(MsgType msgType, String fromUserID, String toUserId) {
        Message mMessgae = new Message();
        mMessgae.setUuid(UUID.randomUUID() + "");
        mMessgae.setSenderId(fromUserID);
        mMessgae.setTargetId(toUserId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    private Message getBaseReceiveMessage(MsgType msgType, String fromUserID, String toUserId) {
        Message mMessgae = new Message();
        mMessgae.setUuid(UUID.randomUUID() + "");
        mMessgae.setSenderId(fromUserID);
        mMessgae.setTargetId(toUserId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(final String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }


}
/*

private ByteString getByteString(File file){
    byte[] buffer = null;
    try {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        byte[] b = new byte[1000];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        buffer = bos.toByteArray();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return ByteString.of(buffer,0,buffer.length);
}

 */