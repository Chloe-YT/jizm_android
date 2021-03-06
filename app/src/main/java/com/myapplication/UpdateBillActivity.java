package com.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dao.AccountDAO;
import dao.AccountDAOImpl;
import dao.BillDAO;
import dao.BillDAOImpl;
import dao.CategoryDAO;
import dao.CategoryDAOImpl;
import pojo.Account;
import pojo.Bill;
import pojo.Category;

/*
实现修改账单
 */
public class UpdateBillActivity extends AppCompatActivity implements View.OnClickListener{

    Bill bill;
    ArrayList<Category> categories;
    ArrayList<Account> accounts;

    //这个数组用Category数组的名称来初始化，然后通过选择的下标来判定选了那个Periodic  5.3  0:06
//    public ArrayList<String> listData = new  ArrayList<String>();
    public ArrayList<String> outcomeListData = new ArrayList<String>();
    public ArrayList<String> incomeListData = new ArrayList<String>();
    public ArrayList<Integer> outcomeListId = new ArrayList<Integer>();
    public ArrayList<Integer> incomeListId = new ArrayList<Integer>();
   // private TextView view ;
    private MaterialSpinner spinner;
    private ArrayAdapter<String> adapter;

    public ArrayList<String> listAccount=new  ArrayList<String>();
    //private TextView accountView ;
    private MaterialSpinner accountSpinner;
    private ArrayAdapter<String> accountAdapter;

    Button incomeButton;
    Button outComeButton;
    SuperButton saveBillButton;
    ImageView backIv;      //返回键

    //名称和金额
    EditText BillNameEdit;
    EditText BillMoneyEdit;

    //时间
    private RoundButton billUpdateTime;
    protected String days;
    public int mYear;
    public int mMonth;
    public int mDays;

    //辅助变量
    public int categoryId;
    public int accountId;

    //属性
    public int isIncome = 0;         //记录类别（收入/支出）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bill);

        init();
        setData();

        //将可选内容与ArrayAdapter连接起来
        if(isIncome == 0) {
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, outcomeListData);
        } else {
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, incomeListData);
        }

        accountAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listAccount);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
        accountSpinner.setAdapter(accountAdapter);

        //spinner数据加载完，设置一下默认选中的值
        setDefaultPsinnerItem();
        setDefaultAccountItem();

        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListenerBup());
        accountSpinner.setOnItemSelectedListener(new BillAccountSelectedListener());

        //设置默认值
        spinner.setVisibility(View.VISIBLE);
        accountSpinner.setVisibility(View.VISIBLE);

    }

    public void init(){
        //view = (TextView) findViewById(R.id.bill_update_spinnerText);
        //accountView=(TextView)findViewById(R.id.bill_account_spinnerText);
        spinner = (MaterialSpinner) findViewById(R.id.spinner_category);
        accountSpinner=(MaterialSpinner)findViewById(R.id.spinner_account);

        //返回按钮
        backIv = (ImageView)findViewById(R.id.back_btn1);

        //输入支出按钮
        incomeButton = (Button)findViewById(R.id.bill_update_income);
        outComeButton = (Button)findViewById((R.id.bill_update_outcome));
        saveBillButton = (SuperButton)findViewById(R.id.btn_save) ;

        //选择时间
        billUpdateTime=(RoundButton)findViewById(R.id.bill_time);

        //账单名称和金额
        BillNameEdit = (EditText)findViewById(R.id.bill_name) ;
        BillMoneyEdit = (EditText)findViewById(R.id.bill_money) ;

        //设置事件监听者
        backIv.setOnClickListener(this);
        billUpdateTime.setOnClickListener(this);
        incomeButton.setOnClickListener(this);
        outComeButton.setOnClickListener(this);
        saveBillButton.setOnClickListener(this);
    }

    /*
    设置下拉列表默认选中的值
     */
    public void setDefaultPsinnerItem(){
        //dao的代码还没好，先注释掉
        //拿着periodic的category_id取得Category，然后用category名字和listData做对比得到选中的值
        int catId = bill.getCategory_id();
        CategoryDAO catDAO = new CategoryDAOImpl();
        Category category = catDAO.getCategoryById(catId);
        String categoryName = category.getCategory_name();


        /*if (isIncome == 0) {
            view.setText(outcomeListData.get(position));
            categoryId = outcomeListId.get(position);
        } else {
            view.setText(incomeListData.get(position));
            categoryId = incomeListId.get(position);
        }*/



        if (isIncome == 0) {
            for(int i=0;i<outcomeListData.size();i++){
                if(categoryName.equals(outcomeListData.get(i))){
                    //spinner.setSelection(i);
                    spinner.setSelectedIndex(i);
                    categoryId = outcomeListId.get(i);
                    break;
                }
            }
        } else {
            for(int i=0;i<incomeListData.size();i++){
                if(categoryName.equals(incomeListData.get(i))){
                    //spinner.setSelection(i);
                    spinner.setSelectedIndex(i);
                    categoryId = incomeListId.get(i);
                    break;
                }
            }
        }
        //spinner.setSelection(1);
    }

    public void setDefaultAccountItem(){
        //dao的代码还没好，先注释掉
        int actId = bill.getAccount_id();
        AccountDAO actDAO = new AccountDAOImpl();
        Account account = actDAO.getAccountById(actId);
        String accountName = account.getAccount_name();

        for(int i=0;i<listAccount.size();i++){
            if(accountName.equals(listAccount.get(i))){
                //accountSpinner.setSelection(i);
                accountSpinner.setSelectedIndex(i);
                accountId = bill.getAccount_id();

                break;
            }
        }

        //后面删除
        //accountSpinner.setSelection(1);
    }

    public void setData(){

        String tmpId=getIntent().getStringExtra("billId");
        int id = Integer.parseInt(tmpId);

        BillDAO billDAO = new BillDAOImpl();
        bill= billDAO.getBillById(id);

        //设置开始结束时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        days = formatter.format(bill.getBill_date());

        billUpdateTime.setText(days);

        //获取当前年，月，日
        Calendar calendar = Calendar.getInstance();
        //开始时间
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDays = calendar.get(Calendar.DAY_OF_MONTH);

        //设置按钮选中
        if(bill.getType()==0){//0是支出
            outComeButton.setSelected(true);
            incomeButton.setSelected(false);
            isIncome = 0;
            // outComeButton.setBackgroundColor();
        }
        else {//1是收入
            incomeButton.setSelected(true);
            outComeButton.setSelected(false);
            isIncome = 1;
            // incomeButton.setBackgroundColor();
        }

        //设置账目名称和金额输入框内容
        BillNameEdit.setText(bill.getBill_name());
        BillMoneyEdit.setText(String.valueOf(bill.getBill_money()));

        //设置类别值
        CategoryDAO categoryDAO=new CategoryDAOImpl();
        //categories = (ArrayList<Category>) categoryDAO.listCategory();


        //筛选未删除的类别
        List<Category> tmpList = categoryDAO.listCategory();
        List<Category> newData = new ArrayList<Category>();
        for(Category item:tmpList){
            if(item.getState()!=-1){
                newData.add(item);
            }
        }
        categories = (ArrayList<Category>)newData;



//        for(Category cat:categories){
//            listData.add(cat.getCategory_name());
//        }

        //设置收入支出切换时类别切换
        for(int i = 0; i < categories.size(); i++){
            Category category = (Category)categories.get(i);
            if (category.getType() == 0) {
                outcomeListData.add(category.getCategory_name());
                outcomeListId.add(category.getCategory_id());
            } else {
                incomeListData.add(category.getCategory_name());
                incomeListId.add(category.getCategory_id());
            }
        }



        //设置账户值
        AccountDAO accountDAO=new AccountDAOImpl();
        //accounts = (ArrayList<Account>) accountDAO.listAccount();


       //筛选未被删除的账户
       List<Account> tmpAccounts = accountDAO.listAccount();
       List<Account> newAccounts = new ArrayList<Account>();
        for(Account item:tmpAccounts){
            if(item.getState()!=-1){
                newAccounts.add(item);
            }
        }

        accounts = (ArrayList<Account>)newAccounts;


        for(Account act:accounts){
            listAccount.add(act.getAccount_name());
        }




    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn1:
                finish();
                break;

            case R.id.bill_time:
                showStartDateSelector();
                break;

            case R.id.bill_update_outcome:
                setBillType(0);//0代表支出
                isIncome = 0;
                adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, outcomeListData);
                adapter.notifyDataSetChanged();
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                setDefaultPsinnerItem();
                spinner.setOnItemSelectedListener(new SpinnerSelectedListenerBup());



                spinner.setVisibility(View.VISIBLE);
                break;
            case R.id.bill_update_income:
                setBillType(1);//1代表收入
                isIncome = 1;
                adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, incomeListData);
                adapter.notifyDataSetChanged();
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                setDefaultPsinnerItem();
                spinner.setOnItemSelectedListener(new SpinnerSelectedListenerBup());
                spinner.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_save:
                if(saveBill()){
                    Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                    //把值periodicId传回上一个界面
                    Intent intent = new Intent();
                    intent.putExtra("id_return_bill",String.valueOf(bill.getBill_id()));
                    setResult(RESULT_OK,intent);
                    this.finish();
                }
                break;

            default:
                break;

        }

    }


    //内部类，下拉列表监听者，使用数组形式操作
    //MaterialSpinner.OnItemSelectedListener
    class SpinnerSelectedListenerBup implements MaterialSpinner.OnItemSelectedListener {

       /* public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (isIncome == 0) {
                view.setText(outcomeListData.get(arg2));
                categoryId = outcomeListId.get(arg2);
            } else {
                view.setText(incomeListData.get(arg2));
                categoryId = incomeListId.get(arg2);
            }
        }*/

        public void onNothingSelected(AdapterView<?> arg0) {
        }

        @Override
        public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
            if (isIncome == 0) {
                view.setText(outcomeListData.get(position));
                categoryId = outcomeListId.get(position);
            } else {
                view.setText(incomeListData.get(position));
                categoryId = incomeListId.get(position);
            }
        }
    }

    //内部类，下拉列表监听者，使用数组形式操作
    class BillAccountSelectedListener implements MaterialSpinner.OnItemSelectedListener {

       /* public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            //accountView.setText(listAccount.get(arg2));

            //设置Account_id 记得去掉注释
            accountId = accounts.get(arg2).getAccount_id();
        }*/

        public void onNothingSelected(AdapterView<?> arg0) {
        }

        @Override
        public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
            //accountView.setText(listAccount.get(position));
            view.setText(listAccount.get(position));
            //设置Account_id 记得去掉注释
            accountId = accounts.get(position).getAccount_id();
        }
    }

    //显示开始日期选择器
    public void showStartDateSelector() {
        new DatePickerDialog(this,(DatePicker datePicker, int i, int i1, int i2) -> {
            mYear = i;
            mMonth = i1;
            mDays = i2;
            if (mMonth + 1 < 10) {
                if (mDays < 10) {
                    days = new StringBuffer().append(mYear).append("-").append("0")
                            .append(mMonth + 1).append("-").append("0").append(mDays).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").append("0")
                            .append(mMonth + 1).append("-").append(mDays).toString();
                }
            } else{
                if (mDays < 10) {
                    days = new StringBuffer().append(mYear).append("-")
                            .append(mMonth + 1).append("-").append("0").append(mDays).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-")
                            .append(mMonth + 1).append("-").append(mDays).toString();
                }
            }

            billUpdateTime.setText(days);

        },mYear,mMonth,mDays).show();
    }

    public void setBillType(int type){
        if(type==0){//支出
            outComeButton.setSelected(true);
            incomeButton.setSelected(false);
            bill.setType(0);
        }
        else {//1代表收入
            incomeButton.setSelected(true);
            outComeButton.setSelected(false);
            bill.setType(1);
        }
    }

    public void setBillCategory(){
        //设置categoryId
        bill.setCategory_id(categoryId);
    }

    public void setBillAccount(){
        bill.setAccount_id(accountId);
    }

    public boolean review(){
       /*
       检查数据是否达到存入要求
        */
        AlertDialog.Builder builder  = new AlertDialog.Builder(UpdateBillActivity.this);
        builder.setTitle("确认" ) ;
        builder.setMessage("账单信息有误，请检查" ) ;
        builder.setPositiveButton("是" ,  null );

        Log.i(BillMoneyEdit.getText().toString()+"  ",BillNameEdit.getText().toString());
        //输入框为空
        if(TextUtils.isEmpty(BillMoneyEdit.getText())|| TextUtils.isEmpty(BillNameEdit.getText())){

            Log.i(BillMoneyEdit.getText().toString()+"  ",BillNameEdit.getText().toString());
            builder.setMessage("账单信息名称或金额为空！" ) ;
            builder.show();
            return false;
        }

        //检查时间是否超过现在
        Log.i("days: ",days);
        try {
            Date date= new SimpleDateFormat("yyyy-MM-dd").parse(days);
            Date now = new Date();
            if(date.compareTo(now)==1){//输入时间比当前时间大，错误
                builder.setMessage("时间不能大于当前" ) ;
                builder.show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String moneyStr=BillMoneyEdit.getText().toString();
        if(moneyStr.startsWith("-")){
            builder.show();
            return false;
        }
        return true;
    }

    public boolean saveBill(){

        if(!review()){ //检查是否有为空的数据，不合法的数据等,不合法则直接返回
            return false;
        }

        //设置名称和金额
        String moneyStr=BillMoneyEdit.getText().toString();

        bill.setBill_name(BillNameEdit.getText().toString());
        bill.setBill_money(Double.valueOf(moneyStr));

        //设置种类
        setBillCategory();
        //设置账户
        setBillAccount();

        //设置时间
        Date date= null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(days);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        bill.setBill_date(date);

        //设置状态码
        bill.setState(1);

      /*  Log.i("bill_name: ",bill.getBill_name());
        Log.i("bill_money: ",String.valueOf(bill.getBill_money()));
        Log.i("bill_type: ",String.valueOf(bill.getType()));
        Log.i("bill_category: ",String.valueOf(bill.getCategory_id()));
        Log.i("bill_recycle: ",String.valueOf(bill));*/

        //存入数据库 暂时注解掉
        BillDAO billDAO = new BillDAOImpl();
        billDAO.updateBill(bill);

        return true;
    }
}
