package ca.bcit.comp7082.zilong.photogallery;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.bcit.comp7082.zilong.photogallery.models.QueryPicture;

public class SearchActivity extends AppCompatActivity {

    //UI References
    private EditText fromDate;
    private EditText toDate;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        fromDate = findViewById(R.id.from_date_input);
        toDate = findViewById(R.id.to_date_input);

        fromDatePickerDialog = bindDatePickerDialog(this, fromDate);
        toDatePickerDialog = bindDatePickerDialog(this, toDate);
    }

    public static DatePickerDialog bindDatePickerDialog(Context context, final EditText editText) {

        Calendar newCalendar = Calendar.getInstance();

        return new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                newDate.set(year, monthOfYear, dayOfMonth);
                editText.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

//    @Override
//    public void onBackPressed() {
//       // do nothing
//    }

    public void onFromDateClick(View v) {
        fromDatePickerDialog.show();
    }

    public void onToDateClick(View v) {
        toDatePickerDialog.show();
    }

    public void onSearchClick(View v) {
        returnFormResult();
    }

    public void returnFormResult() {
        QueryPicture query = new QueryPicture();
        EditText keyword = findViewById(R.id.keyword_input);

        query.setTitle(keyword.getText().toString());
        query.setFromDate(getDateFromDatePicker(fromDatePickerDialog, 0));
        query.setToDate(getDateFromDatePicker(toDatePickerDialog, 1)); // offset to 0 o'clock of next day

        Intent resultIntent = new Intent();
        resultIntent.putExtra("queryData", query);
        setResult(RESULT_OK, resultIntent);
        finish();
        super.onBackPressed();
    }

    /**
     * @param datePicker
     * @return a java.util.Date
     */
    public static Date getDateFromDatePicker(DatePickerDialog datePicker, int offsetDay) {
        int day = datePicker.getDatePicker().getDayOfMonth();
        int month = datePicker.getDatePicker().getMonth();
        int year = datePicker.getDatePicker().getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day + offsetDay, 0, 0, 0);

        return calendar.getTime();
    }

}
