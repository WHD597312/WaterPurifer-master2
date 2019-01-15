package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.waterpurifer.MainActivity;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.adapter.CityAdapter;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.bean.City;
import com.peihou.waterpurifer.bean.District;
import com.peihou.waterpurifer.bean.Province;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.ToastUtil;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.OnClick;


public class ChangAddressActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.et_address_adressxq)
    EditText et_address_adressxq;
    @BindView(R.id.tv_user_adresxq)
    TextView tv_user_adresxq;
    @BindView(R.id.tv_chang_adress)
            TextView tv_chang_adress;
    String adress1,adress2 ;
    private PopupWindow mPopWindow;
    int sign_sheng = 0, sign_city = 0, isDefault = 1, receiveId = 0;
    String receiveProvince, receiveCity, receiveCounty, receiveAddress;
    private ProgressDialog progressDialog;
    private View contentViewSign;
    List<Province> list = null;
    Province province = null;
    List<City> cities = null;
    City city = null;
    List<District> districts = null;
    District district = null;
    SharedPreferences preferences;
    String id,address;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_changraddress;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        progressDialog = new ProgressDialog(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        id = preferences.getString("userId","");
        address = preferences.getString("address","");
    }

    @Override
    public void doBusiness(Context mContext) {
        tv_chang_adress.setText(address);
    }

    @Override
    public void widgetClick(View v) {

    }
    ChangpassAsyncTask task;
    @OnClick({R.id.iv_main_memu,R.id.bt_adress_add,R.id.rl_user_address})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_main_memu:
                finish();
                break;

            case R.id.bt_adress_add:
                adress1 = tv_user_adresxq.getText().toString();
                adress2 =et_address_adressxq.getText().toString().trim();
                if (TextUtils.isEmpty(adress1)) {
                    ToastUtil.showShort(this, "请选择地址");
                    break;
                }
                if (TextUtils.isEmpty(adress2)) {
                    ToastUtil.showShort(this, "请输入详细地址");
                    break;
                }

                    Map<String, Object> params = new HashMap<>();
                    params.put("address", adress1+adress2);
                    params.put("userId", id);
                        task = new ChangpassAsyncTask();
                        task.execute(params);
                        new Thread() {
                            public void run() {
                                try {
                                    task.get(5, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                } catch (ExecutionException e) {
                                } catch (TimeoutException e) {
                                    Message message = new Message();
                                    message.obj = "TimeOut";
                                    handler.sendMessage(message);
                                }
                            }
                        }.start();

                break;
            case R.id.rl_user_address:
                showPopup();
                break;
        }
    }
    @SuppressLint("StaticFieldLeak")
    class ChangpassAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @SafeVarargs
        @Override
        protected final String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> params = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/user/updateUser",params);
            Log.e("result", "doInBackground: -->"+result );
            if (!ToastUtil.isEmpty(result)) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("returnCode");

                    if ("100".equals(code)){
                    JSONObject returnData = jsonObject.getJSONObject("returnData");
                     SharedPreferences.Editor editor = preferences.edit();
                     editor.putString("address",adress1+adress2);
                     editor.commit();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s) {

                case "100":
                    tv_chang_adress.setText(adress1+adress2);
                    startActivity(MainActivity.class);
                    ToastUtil.showShort(ChangAddressActivity.this, "修改成功");

                    break;
                default:
                    ToastUtil.showShort(ChangAddressActivity.this, "修改失败，请重试");
                    break;
            }
        }
    }

    private RecyclerView recyclerview;

    private View view_dis;
    private ListView listCity;
    private List<String> data = new ArrayList<>();
    private CityAdapter cityAdapter;
    private TextView tv_sheng, tv_shi, tv_qu;
    private RelativeLayout rl_sheng, rl_shi, rl_qu;
    private ImageView img_sheng, img_shi, img_qu ,iv_dia_close;
    private ImageView[] img_city;
    private int sing_city = 0;
    private int p1=-1;
    private int p2=-1;
    private int p3=-1;
    private void showPopup() {
        parser();
        contentViewSign = LayoutInflater.from(this).inflate(R.layout.popup_shop_city, null);
        view_dis = contentViewSign.findViewById(R.id.view_dis);
        listCity = (ListView) contentViewSign.findViewById(R.id.list_city);
        tv_sheng = (TextView) contentViewSign.findViewById(R.id.tv_sheng);
        tv_shi = (TextView) contentViewSign.findViewById(R.id.tv_shi);
        tv_qu = (TextView) contentViewSign.findViewById(R.id.tv_qu);
        rl_sheng = (RelativeLayout) contentViewSign.findViewById(R.id.rl_sheng);
        rl_shi = (RelativeLayout) contentViewSign.findViewById(R.id.rl_shi);
        rl_qu = (RelativeLayout) contentViewSign.findViewById(R.id.rl_qu);
        img_sheng = (ImageView) contentViewSign.findViewById(R.id.img_sheng);
        img_shi = (ImageView) contentViewSign.findViewById(R.id.img_shi);
        img_qu = (ImageView) contentViewSign.findViewById(R.id.img_qu);
        iv_dia_close= contentViewSign.findViewById(R.id.iv_dia_close);
        img_city = new ImageView[]{img_sheng, img_shi, img_qu};

        view_dis.setOnClickListener(this);
        rl_sheng.setOnClickListener(this);
        rl_shi.setOnClickListener(this);
        rl_qu.setOnClickListener(this);
        iv_dia_close.setOnClickListener(this);
        rl_sheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing_city=0;
                upData(0);
                img_sheng.setImageResource(R.mipmap.dz_blue);
                img_shi.setImageResource(R.mipmap.dz_gray);
                img_qu.setImageResource(R.mipmap.dz_gray);
                cityAdapter.setCurrentItem(p1);

            }
        });
        rl_shi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing_city=1;
                upData(1);
                img_sheng.setImageResource(R.mipmap.dz_gray);
                img_shi.setImageResource(R.mipmap.dz_blue);
                img_qu.setImageResource(R.mipmap.dz_gray);
                cityAdapter.setCurrentItem(p2);
            }
        });
        rl_qu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing_city=2;
                upData(2);
                img_sheng.setImageResource(R.mipmap.dz_gray);
                img_shi.setImageResource(R.mipmap.dz_gray);
                img_qu.setImageResource(R.mipmap.dz_blue);
                cityAdapter.setCurrentItem(p3);
            }
        });
        iv_dia_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        cityAdapter = new CityAdapter(data, this);
        listCity.setAdapter(cityAdapter);
        listCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (sing_city) {
                    case 0:
                        receiveProvince = data.get(position);
                        tv_sheng.setText(receiveProvince);
                        sign_sheng = position;
                        img_sheng.setImageResource(R.mipmap.dz_gray);
                        img_shi.setImageResource(R.mipmap.dz_blue);
                        p1=position;
                        upData(1);
                        break;
                    case 1:
                        receiveCity = data.get(position);
                        tv_shi.setText(receiveCity);
                        sign_city = position;
                        p2=position;
                        img_shi.setImageResource(R.mipmap.dz_gray);
                        img_qu .setImageResource(R.mipmap.dz_blue);
                        upData(2);
                        break;
                    case 2:
                        receiveCounty = data.get(position);
                        tv_qu.setText(receiveCounty);
                        mPopWindow.dismiss();
                        p3=position;

                        if (tv_sheng.getText().equals("省")||tv_shi.getText().equals("市")){
                            adress1="";
                            tv_user_adresxq.setText("");
                            ToastUtil.showShort(ChangAddressActivity.this,"请选择正确的地址");
                        }else {
                            adress1=tv_sheng.getText().toString() + tv_shi.getText().toString() + tv_qu.getText().toString();
                            tv_user_adresxq.setText(tv_sheng.getText().toString()+ tv_shi.getText().toString() +tv_qu.getText().toString());
                        }
                        break;


                }

            }

        });
        upData(0);
        mPopWindow = new PopupWindow(contentViewSign);

        mPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        mPopWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        //点击空白处时，隐藏掉pop窗口
        mPopWindow.setFocusable(true);
//        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow. setOutsideTouchable(true);
        mPopWindow.setClippingEnabled(false);
        backgroundAlpha(0.5f);
        //添加pop窗口关闭事件
        mPopWindow.setAnimationStyle(R.style.Popupwindow);
        mPopWindow.setOnDismissListener(new poponDismissListener());
//        mPopWindow.showAsDropDown(findViewById(R.id.li_main_bt));
        mPopWindow.showAtLocation(tv_user_adresxq, Gravity.BOTTOM, 0, 0);
    }



    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            backgroundAlpha(1f);
        }

    }
    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    private void upData(int i) {

//        img_city[sing_city].setVisibility(View.INVISIBLE);
        sing_city = i;
//        img_city[sing_city].setVisibility(View.VISIBLE);
        data.clear();
        if (i == 0) {
            for (int a = 0; a < list.size(); a++) {
                data.add(list.get(a).getName());
            }
        } else if (i == 1) {
//            listCity
            if (list.size() > 0) {
                cities = list.get(sign_sheng).getCitys();
                for (int a = 0; a < cities.size(); a++) {
                    data.add(cities.get(a).getName());
                }
            } else {
                Toast.makeText(this, "请选择省份", Toast.LENGTH_SHORT).show();
            }
        } else if (i == 2) {
            if (cities.size() > 0) {
                districts = cities.get(sign_city).getDistricts();
                for (int a = 0; a < districts.size(); a++) {

                        data.add(districts.get(a).getName());
                }
            } else {
                Toast.makeText(this, "请选择城市", Toast.LENGTH_SHORT).show();
            }
        }

        cityAdapter.notifyDataSetChanged();
    }

    public List<Province> parser() {
        // 创建解析器，并制定解析的xml文件
        XmlResourceParser parser = getResources().getXml(R.xml.cities);
        try {
            int type = parser.getEventType();
            while (type != 1) {
                String tag = parser.getName();//获得标签名
                switch (type) {
                    case XmlResourceParser.START_DOCUMENT:
                        list = new ArrayList<Province>();
                        break;
                    case XmlResourceParser.START_TAG:
                        if ("p".equals(tag)) {
                            province = new Province();
                            cities = new ArrayList<City>();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                //获得属性的名和值
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("p_id".equals(name)) {
                                    province.setId(value);
                                }
                            }
                        }
                        if ("pn".equals(tag)) {//省名字
                            province.setName(parser.nextText());
                        }
                        if ("c".equals(tag)) {//城市
                            city = new City();
                            districts = new ArrayList<District>();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("c_id".equals(name)) {
                                    city.setId(value);
                                }
                            }
                        }
                        if ("cn".equals(tag)) {
                            city.setName(parser.nextText());
                        }
                        if ("d".equals(tag)) {
                            district = new District();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("d_id".equals(name)) {
                                    district.setId(value);
                                }
                            }
                            district.setName(parser.nextText());
                            districts.add(district);
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        if ("c".equals(tag)) {
                            city.setDistricts(districts);
                            cities.add(city);
                        }
                        if ("p".equals(tag)) {
                            province.setCitys(cities);
                            list.add(province);
                        }
                        break;
                    default:
                        break;
                }
                type = parser.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */ catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 3) {
                showPopup();
            }
            if ("TimeOut".equals(msg.obj)){
                if (progressDialog!=null&&progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(ChangAddressActivity.this,"请求超时,请重试",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null)
            handler.removeCallbacksAndMessages(null);
    }
}
