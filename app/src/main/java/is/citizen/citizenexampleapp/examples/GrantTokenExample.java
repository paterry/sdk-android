package is.citizen.citizenexampleapp.examples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import is.citizen.citizenapi.async.tokens.RespondTokenTask;
import is.citizen.citizenapi.enums.TokenStatus;
import is.citizen.citizenapi.resource.Token;
import is.citizen.citizenapi.service.CryptoService;
import is.citizen.citizenapi.service.TokenService;
import is.citizen.citizenapi.util.Constant;

import is.citizen.citizenexampleapp.R;

/*
 * Grant a token.
 *
 * The following steps happen on the Citizen Service when granting a token:
 *
 *   1) Requested data is decrypted using the user's private key.
 *
 *   2) Requested data is encrypted using the requesting user's public key.
 *
 *   3) Token is added to the requesting user's token store.
 *
 * The token that is returned from this call has encrypted data, since it has
 * been encrypted with the requesting user's public key.
 *
 */

public class GrantTokenExample extends Activity
    implements RespondTokenTask.AsyncResponse
{
    private static final String TAG = GrantTokenExample.class.getSimpleName();

    private TextView exampleDescription;
    private TextView resultDescription;
    private TextView resultValue;
    private TextView nextExampleText;
    private FloatingActionButton nextExampleButton;
    private EditText numericalInput;
    private Spinner spinnerInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_default);

        final Activity activity = this;

        final CryptoService cryptoService = new CryptoService();
        final TokenService tokenService = new TokenService(cryptoService);
        final RespondTokenTask.AsyncResponse callback = this;

        exampleDescription = (TextView) findViewById(R.id.example_description);
        resultDescription = (TextView) findViewById(R.id.example_result_description);
        resultValue = (TextView) findViewById(R.id.example_result_value);
        nextExampleText = (TextView) findViewById(R.id.example_next_example);
        nextExampleButton = (FloatingActionButton) findViewById(R.id.example_next_button);
        numericalInput = (EditText) findViewById(R.id.example_numerical_input);
        spinnerInput = (Spinner) findViewById(R.id.example_spinner);

        exampleDescription.setText("Grant Token Example");
        resultDescription.setText("Token Status:");
        nextExampleText.setVisibility(View.GONE);
        nextExampleButton.setVisibility(View.GONE);
        numericalInput.setVisibility(View.GONE);
        spinnerInput.setVisibility(View.GONE);

        final Bundle params = getIntent().getExtras();
        if (params == null) {
            Log.e(TAG, "Unable to get parameters from previous activity");
            resultValue.setText("Unable to get parameters from previous example.");
            return;
        }

        final String tokenId = params.getString("tokenId_1");
        final String apiKey = params.getString("apiKey");
        final String mnemonic = params.getString("mnemonic");

        final Token token = new Token();
        token.setId(tokenId);
        token.setTokenStatus(TokenStatus.GRANTED);

        FloatingActionButton runExampleButton = (FloatingActionButton) findViewById(R.id.example_run_button);
        runExampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RespondTokenTask respondTokenTask = new RespondTokenTask(callback, tokenService);
                respondTokenTask.execute(token, apiKey, mnemonic);
            }
        });

        FloatingActionButton nextExampleButton = (FloatingActionButton) findViewById(R.id.example_next_button);
        nextExampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DeclineTokenExample.class);
                intent.putExtras(params);
                startActivity(intent);
            }
        });
    }


    public void respondTokenTaskFinished(Integer statusCode, Token token) {
        if (statusCode == Constant.CITIZEN_REST_CODE_SUCCESS) {
            resultValue.setText(token.getTokenStatus().toString());
            nextExampleText.setVisibility(View.VISIBLE);
            nextExampleButton.setVisibility(View.VISIBLE);
        } else {
            resultValue.setText("Error: " + statusCode.toString());
        }
    }
}
