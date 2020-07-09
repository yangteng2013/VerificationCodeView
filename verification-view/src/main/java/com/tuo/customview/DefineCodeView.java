package com.tuo.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

public class DefineCodeView extends LinearLayout implements TextWatcher, View.OnKeyListener, View.OnFocusChangeListener {

    private String TAG = DefineCodeView.class.getSimpleName();

    private Context context;
    private int count ;//个数
    private int size;//显示文字大小
    private int textColor;
    private int editWidth;

    private int focusId;//拥有焦点的EditText的id

    private ResultCallBack callBack;//输入结果回调接口对象引用

    public ResultCallBack getCallBack() {
        return callBack;
    }
    //输入框获取焦点时背景
    private Drawable mEtBackgroundDrawableFocus;
    //输入框没有焦点时背景
    private Drawable mEtBackgroundDrawableNormal;
    public void setCallBack(ResultCallBack callBack) {
        this.callBack = callBack;
    }

    public interface ResultCallBack {//输入结果回调接口
        void backResult(String result);
    }

    public DefineCodeView(Context context) {
        super(context);
    }

    public DefineCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CodeEditText);
        count = typedArray.getInteger(R.styleable.CodeEditText_count, 17);
        size = typedArray.getInteger(R.styleable.CodeEditText_count, (int) context.getResources().getDimension(R.dimen.qb_px_20));
        textColor = typedArray.getColor(R.styleable.CodeEditText_textColor, Color.WHITE);
        mEtBackgroundDrawableFocus = typedArray.getDrawable(R.styleable.CodeEditText_cet_et_bg_focus);
        mEtBackgroundDrawableNormal = typedArray.getDrawable(R.styleable.CodeEditText_cet_et_bg_normal);
        editWidth = (int) typedArray.getDimension(R.styleable.CodeEditText_editWidth, 100);
        //释放资源
        typedArray.recycle();

        if (mEtBackgroundDrawableFocus == null) {
            mEtBackgroundDrawableFocus = context.getResources().getDrawable(R.drawable.shape_icv_et_bg_focus);
        }

        if (mEtBackgroundDrawableNormal == null) {
            mEtBackgroundDrawableNormal = context.getResources().getDrawable(R.drawable.shape_icv_et_bg_normal);
        }

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        for (int i = 0; i < count; i++) {
            PwdEditText editText = new PwdEditText(context);
            initEdit(editText, i);
            addView(editText);
        }
    }

    /**
     * 初始化editText的一些属性
     *
     * @param editText
     * @param i
     */
    private void initEdit(PwdEditText editText, int i) {
        int padding = (int) context.getResources().getDimension(R.dimen.qb_px_20);
        LayoutParams params = new LayoutParams(editWidth, editWidth);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setLayoutDirection(HORIZONTAL);
        }
        params.rightMargin = padding;
        params.topMargin = padding;
        params.bottomMargin = padding;
        params.gravity = Gravity.CENTER;
        editText.setLayoutParams(params);

        editText.setGravity(Gravity.CENTER);
        //基本属性
        editText.setId(i);
        editText.setTextSize(size);
        editText.setTextColor(textColor);
//        editText.setBackgroundResource(R.drawable.shape_icv_et_bg_normal);
        if (i == 0) {
            editText.setBackgroundDrawable(mEtBackgroundDrawableFocus);
        } else {
            editText.setBackgroundDrawable(mEtBackgroundDrawableNormal);
        }
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        //输入个数限制
        editText.setMaxEms(1);
        editText.setMaxLines(1);
        //间距
        editText.setPadding(0, 0, 0, 0);
        //设置监听
        editText.setOnKeyListener(this);
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(this);

        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if (src.equals("")) {
                            return src;
                        }
                        if (src.toString().matches("[a-z0-9A-Z ]+")) {
                            return src.toString().toUpperCase();
                        }
                        return "";
                    }
                },new InputFilter.LengthFilter(1)
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() > 0) {
            focus();
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (callBack!=null){
                callBack.backResult("");
            }
            //在第0个时候点击删除键，不删除焦点框；
            if (focusId>0){
                backFocus(focusId);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean onFocus) {
        if (onFocus) {
            Log.e(TAG,"获取焦点的控件id："+view.getId());
            //获取焦点的控件显示光标
            EditText editText = (EditText) getChildAt(view.getId());
            focusId = view.getId();
            if (editText.getText().length()>=1){
                editText.setCursorVisible(true);
                editText.setBackgroundDrawable(mEtBackgroundDrawableFocus);
                editText.requestFocus();
            }else{
                focus();
            }
        }else {
            EditText editText = (EditText) getChildAt(view.getId());
            editText.setBackgroundDrawable(mEtBackgroundDrawableNormal);
        }
    }

    /**
     * 找无内容控件焦点
     */
    private void focus() {
        int count = getChildCount();
        EditText editText;
        for (int i = 0; i < count; i++) {
            editText = (EditText) getChildAt(i);
            editText.setBackgroundDrawable(mEtBackgroundDrawableNormal);
            if (editText.getText().length() < 1) {
                editText.setCursorVisible(true);
                editText.requestFocus();
                editText.setBackgroundDrawable(mEtBackgroundDrawableFocus);
                return;
            } else {
                editText.setCursorVisible(false);
                editText.clearFocus();
            }
        }
        //如果前面没有空内容控件，且最后一个控件内容不为空，则认为验证码已经输入完全
        EditText lastEdit = (EditText) getChildAt(count - 1);
        if (lastEdit.getText().length() > 0) {
            lastEdit.requestFocus();
            //返回结果
            backResult();
        }
    }

    /**
     * 删除时往上找焦点
     */
    private void backFocus(int index) {
        for (int i = index; i >= 0; i--) {
            EditText editText = (EditText) getChildAt(i);
            editText.setBackgroundDrawable(mEtBackgroundDrawableNormal);
            if (!editText.getText().toString().trim().equals("")) {
                editText.setText("");
                editText.setCursorVisible(true);
                editText.setBackgroundDrawable(mEtBackgroundDrawableFocus);
                editText.requestFocus();
                return;
            }
        }
    }

    /**
     * 返回结果
     */
    private void backResult() {
        StringBuffer stringBuffer = new StringBuffer();
        EditText editText;
        for (int i = 0; i < count; i++) {
            editText = (EditText) getChildAt(i);
            stringBuffer.append(editText.getText());
        }
        //进行回调
        if (null != callBack) {
            Log.e(TAG,"回调结果："+stringBuffer);

            callBack.backResult(stringBuffer.toString());
        }

    }
}