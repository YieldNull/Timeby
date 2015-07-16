package slideeffect;

/**
 * Created by fangdongliang on 15/7/16.
 */

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nectar.timeby.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by fangdongliang on 15/7/16.
 */

/**
 * 联系人分章节显示、ListView快速滑动显示联系人首字母、附带字母表快速查找的例子
 * 查阅网络资源，实现方式都是比较复杂，尤其有些还实现了SectionIndex接口，很多人不怎么能理解，研究后发现此种类型的例子没必要也不应该那么实现
 * @author [email]hiphonezhu@sina.com[/email]
 *
 */
public class FastContactSearchDemoActivity extends Activity {
    private BaseAdapter adapter;
    private ListView personList;
    private List<ContentValues> list;
    private AsyncQueryHandler asyncQuery;
    private QuickAlphabeticBar alpha;
    private static final String NAME = "name", NUMBER = "number",
            SORT_KEY = "sort_key";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        personList = (ListView) findViewById(R.id.listView);
        alpha = (QuickAlphabeticBar) findViewById(R.id.fast_scroller);

        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = Uri.parse("content://com.android.contacts/data/phones"); // 联系人的Uri
        String[] projection = { "_id", "display_name", "data1", "sort_key" }; // 查询的列
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
    }

    /**
     * 数据库异步查询类AsyncQueryHandler
     *
     * @author administrator
     *
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);

        }

        /**
         * 查询结束的回调函数
         */
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                list = new ArrayList<ContentValues>();
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    ContentValues cv = new ContentValues();
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);

                    if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
                        cv.put(NAME, name);
                        cv.put(NUMBER, number.substring(3));
                        cv.put(SORT_KEY, sortKey);
                    } else {
                        cv.put(NAME, name);
                        cv.put(NUMBER, number);
                        cv.put(SORT_KEY, sortKey);
                    }
                    list.add(cv);
                }
                if (list.size() > 0) {
                    setAdapter(list);
                }
            }
        }

    }

    private void setAdapter(List<ContentValues> list) {
        adapter = new ListAdapter(this, list);
        personList.setAdapter(adapter);
        alpha.init(FastContactSearchDemoActivity.this);
        alpha.setListView(personList);
        alpha.setHight(alpha.getHeight());
        alpha.setVisibility(View.VISIBLE);
    }

    private static class ViewHolder {
        TextView alpha;
        TextView name;
        TextView number;
    }

    /*
     * 移植时只需要提供一个已排序的集合list即可
     */
    private class ListAdapter extends BaseAdapter{
        private LayoutInflater inflater;
        private List<ContentValues> list;
        private HashMap<String, Integer> alphaIndexer;//保存每个索引在list中的位置【#-0，A-4，B-10】
        private String[] sections;//每个分组的索引表【A,B,C,F...】

        public ListAdapter(Context context, List<ContentValues> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list; // 该list是已经排序过的集合，有些项目中的数据必须要自己进行排序。
            this.alphaIndexer = new HashMap<String, Integer>();
            this.sections = new String[list.size()];

            for (int i =0; i <list.size(); i++) {
                String name = getAlpha(list.get(i).getAsString(SORT_KEY));
                if(!alphaIndexer.containsKey(name)){//只记录在list中首次出现的位置
                    alphaIndexer.put(name, i);
                }
            }
            Set<String> sectionLetters = alphaIndexer.keySet();
            ArrayList<String> sectionList = new ArrayList<String>(
                    sectionLetters);
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            sectionList.toArray(sections);

            alpha.setAlphaIndexer(alphaIndexer);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.number = (TextView) convertView
                        .findViewById(R.id.number);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ContentValues cv = list.get(position);
            String name = cv.getAsString(NAME);
            String number = cv.getAsString(NUMBER);
            holder.name.setText(name);
            holder.number.setText(number);

            // 当前联系人的sortKey
            String currentStr = getAlpha(list.get(position).getAsString(
                    SORT_KEY));
            // 上一个联系人的sortKey
            String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
                    position - 1).getAsString(SORT_KEY)) : " ";
            /**
             * 判断显示#、A-Z的TextView隐藏与可见
             */
            if (!previewStr.equals(currentStr)) { // 当前联系人的sortKey！=上一个联系人的sortKey，说明当前联系人是新组。
                holder.alpha.setVisibility(View.VISIBLE);
                holder.alpha.setText(currentStr);
            } else {
                holder.alpha.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }

        if (str.trim().length() == 0) {
            return "#";
        }

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]{1}quto;");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase(); // 大写输出
        } else {
            return "#";
        }
    }
}

