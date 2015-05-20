package com.wangxuan.github.customedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class MultiFunctionEditText extends EditText implements
    OnFocusChangeListener {

  public static final String FORMAT_STYLE_BANK = "bank";
  public static final String FORMAT_STYLE_PHONE = "phone";
  public static final String FORMAT_STYLE_CERT = "cert";

  private Context mContext;
  private Drawable mClearDrawable;
  private boolean hasFoucs;
  private boolean banPaste;
  private TextWatcher mTextWatcher;

  public MultiFunctionEditText(Context context) {
    super(context);
    // TODO Auto-generated constructor stub
    init(context, null);
  }

  public MultiFunctionEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    // TODO Auto-generated constructor stub
    init(context, attrs);
  }

  public MultiFunctionEditText(Context context, AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    // TODO Auto-generated constructor stub
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    mContext = context;
    String formatStyle = null;
    if (attrs != null) {
      TypedArray array = context.obtainStyledAttributes(attrs,
          R.styleable.multitextstyle);
      formatStyle = array.getString(R.styleable.multitextstyle_format_style);
      banPaste = array.getBoolean(R.styleable.multitextstyle_ban_paste, false);
      array.recycle();
    }
    mClearDrawable = getCompoundDrawables()[2];
    if (mClearDrawable == null) {
      mClearDrawable = getResources().getDrawable(R.drawable.ic_launcher);
    }
    mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
        mClearDrawable.getIntrinsicHeight());
    setClearIconVisible(false);
    setOnFocusChangeListener(this);
    setBanPaste(banPaste);
    setFormatStyle(formatStyle);
    addTextChangedListener(new CleanNotifyWatcher());
  }

  public void setFormatStyle(String style) {
    if (mTextWatcher != null) {
      removeTextChangedListener(mTextWatcher);
    }
    if (FORMAT_STYLE_BANK.equals(style)) {
      mTextWatcher = new BankCardWatcher();
      addTextChangedListener(mTextWatcher);
    }
    if (FORMAT_STYLE_PHONE.equals(style)) {
      mTextWatcher = new PhoneWatcher();
      addTextChangedListener(mTextWatcher);
    }
    if (FORMAT_STYLE_CERT.equals(style)) {
      mTextWatcher = new CertWatcher();
      addTextChangedListener(mTextWatcher);
    }
  }

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

  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    this.hasFoucs = hasFocus;
    if (hasFocus) {
      setClearIconVisible(getText().length() > 0);
    } else {
      setClearIconVisible(false);
    }
  }

  protected void setClearIconVisible(boolean visible) {
    Drawable right = visible ? mClearDrawable : null;
    setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[0],
        getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
  }

  @Override
  public boolean onTextContextMenuItem(int id) {
    // TODO Auto-generated method stub
    if (banPaste) {
      if (16908322 == id) {
        if (mContext != null) {
          Toast.makeText(mContext, "别粘贴好吗", Toast.LENGTH_SHORT).show();
        }
        return true;
      }
    }
    return super.onTextContextMenuItem(id);
  }

  /**
   * 设置是否禁用粘贴操作
   * 
   * @param ban
   */
  public void setBanPaste(boolean ban) {
    banPaste = ban;
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

  class CertWatcher implements TextWatcher {
    int beforeTextLength = 0;
    int onTextLength = 0;
    boolean isChanged = false;

    int location = 0;// 记录光标的位置
    private char[] tempChar;
    private final StringBuffer buffer = new StringBuffer();
    int konggeNumberB = 0;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {
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
      onTextLength = s.length();
      buffer.append(s.toString());
      if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
        isChanged = false;
        return;
      }
      isChanged = true;
    }

    @Override
    public void afterTextChanged(Editable s) {
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
          if ((index == 6 || index == 15)) {
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
  }

  class PhoneWatcher implements TextWatcher {
    int beforeTextLength = 0;
    int onTextLength = 0;
    boolean isChanged = false;

    int location = 0;// 记录光标的位置
    private char[] tempChar;
    private final StringBuffer buffer = new StringBuffer();
    int konggeNumberB = 0;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {
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
      onTextLength = s.length();
      buffer.append(s.toString());
      if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
        isChanged = false;
        return;
      }
      isChanged = true;
    }

    @Override
    public void afterTextChanged(Editable s) {
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
          if ((index == 3 || index == 8)) {
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
  }

  public String getTextWithoutSpace() {
    String bankCardNo = getText().toString().trim();
    StringBuffer sb = new StringBuffer(bankCardNo);
    int index = 0;
    while (index < sb.length()) {
      if (sb.charAt(index) == ' ') {
        sb.deleteCharAt(index);
      } else {
        index++;
      }
    }
    bankCardNo = sb.toString();
    return bankCardNo;
  }

}
