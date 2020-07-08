package jack.com.verificationcodeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tuo.customview.VerificationCodeView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout content;
    private VerificationCodeView icv;
    private jack.com.verificationcodeview.view.VerificationCodeView viewVerification;
    private Button btnSubmit;
    private Button btnClear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content = (LinearLayout) findViewById(R.id.content);
        icv = (VerificationCodeView) findViewById(R.id.icv);

        viewVerification = (jack.com.verificationcodeview.view.VerificationCodeView) findViewById(R.id.view_verification);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnClear = (Button) findViewById(R.id.btn_clear);

        btnSubmit.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        final VerificationCodeView codeView = new VerificationCodeView(this);


        content.addView(codeView);

        icv.setInputCompleteListener(new VerificationCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                Log.i("icv_input", icv.getInputContent());
            }

            @Override
            public void deleteContent() {
                Log.i("icv_delete", icv.getInputContent());
            }
        });


        codeView.postDelayed(new Runnable() {
            @Override
            public void run() {
                codeView.setEtNumber(5);
            }
        }, 5000);



        codeView.setInputCompleteListener(new VerificationCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                Log.i("icv_input", codeView.getInputContent());
            }

            @Override
            public void deleteContent() {
                Log.i("icv_delete", codeView.getInputContent());
            }
        });


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if(viewVerification.isFinish()) {
                    Toast.makeText(this,"输入验证码是:"+viewVerification.getContent(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"请输入完整验证码",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_clear:
                viewVerification.clear();
                break;
        }
    }
    public void onClickClear(View view) {
        icv.clearInputContent();
    }
}
