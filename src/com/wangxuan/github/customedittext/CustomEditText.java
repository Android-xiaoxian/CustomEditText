package com.wangxuan.github.customedittext;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class CustomEditText extends EditText implements OnFocusChangeListener {
  /**
   * 删除按钮的引用
   */
  private Drawable mClearDrawable;
  /**
   * 控件是否有焦点
   */
  private boolean hasFoucs;

  public CustomEditText(Context context) {
    this(context, null);
  }

  public CustomEditText(Context context, AttributeSet attrs) {
    // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
    this(context, attrs, android.R.attr.editTextStyle);
  }

  public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
    mClearDrawable = getCompoundDrawables()[2];
    if (mClearDrawable == null) {
      // throw new
      // NullPointerException("You can add drawableRight attribute in XML");
      mClearDrawable = getResources().getDrawable(R.drawable.ic_launcher);
    }

    mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
        mClearDrawable.getIntrinsicHeight());
    // 默认设置隐藏图标
    setClearIconVisible(false);
    // 设置焦点改变的监听
    setOnFocusChangeListener(this);
    // 设置输入框里面内容发生改变的监听
    addTextChangedListener(new BankCardWatcher());
    addTextChangedListener(new CleanNotifyWatcher());
  }

  /**
   * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
   * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_UP) {
      if (getCompoundDrawables()[2] != null) {

        boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
            && (event.getX() < ((getWidth() - getPaddingRight())));

        if (touchable) {
          this.setText("");
        }
      }
    }

    return super.onTouchEvent(event);
  }

  /**
   * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
   */
  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    this.hasFoucs = hasFocus;
    if (hasFocus) {
      setClearIconVisible(getText().length() > 0);
    } else {
      setClearIconVisible(false);
    }
  }

  /**
   * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
   * 
   * @param visible
   */
  protected void setClearIconVisible(boolean visible) {
    Drawable right = visible ? mClearDrawable : null;
    setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
        right, getCompoundDrawables()[3]);
  }

  class CleanNotifyWatcher implements TextWatcher {

    @Override
    public void afterTextChanged(Editable s) {
      // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      // TODO Auto-generated method stub
      if (hasFoucs) {
        setClearIconVisible(s.length() > 0);
      }
    }

  }

  class BankCardWatcher implements TextWatcher {
    int beforeTextLength = 0;
    int onTextLength = 0;
    boolean isChanged = false;

    int location = 0;// 记录光标的位置
    private char[] tempChar;
    private final StringBuffer buffer = new StringBuffer();
    int konggeNumberB = 0;

    @Override
    public void afterTextChanged(Editable s) {
      // TODO Auto-generated method stub
      if (isChanged) {
        location = getSelectionEnd();
        int index = 0;
        while (index < buffer.length()) {
          if (buffer.charAt(index) == ' ') {
            buffer.deleteCharAt(index);
          } else {
            index++;
          }
        }

        index = 0;
        int konggeNumberC = 0;
        while (index < buffer.length()) {
          if ((index == 4 || index == 9 || index == 14 || index == 19)) {
            buffer.insert(index, ' ');
            konggeNumberC++;
          }
          index++;
        }

        if (konggeNumberC > konggeNumberB) {
          location += (konggeNumberC - konggeNumberB);
        }

        tempChar = new char[buffer.length()];
        buffer.getChars(0, buffer.length(), tempChar, 0);
        String str = buffer.toString();
        if (location > str.length()) {
          location = str.length();
        } else if (location < 0) {
          location = 0;
        }

        setText(str);
        Editable etable = getText();
        Selection.setSelection(etable, location);
        isChanged = false;
      }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {
      // TODO Auto-generated method stub
      beforeTextLength = s.length();
      if (buffer.length() > 0) {
        buffer.delete(0, buffer.length());
      }
      konggeNumberB = 0;
      for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) == ' ') {
          konggeNumberB++;
        }
      }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      // TODO Auto-generated method stub
      onTextLength = s.length();
      buffer.append(s.toString());
      if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
        isChanged = false;
        return;
      }
      isChanged = true;
    }

  }

}
