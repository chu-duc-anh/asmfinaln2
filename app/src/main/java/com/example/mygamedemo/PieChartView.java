package com.example.mygamedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PieChartView extends View {

    private Paint paint;
    private float expensePercentage = 0f;
    private float incomePercentage = 0f;
    private float expenseStartAngle = 0f;
    private float incomeStartAngle = 0f;

    private boolean showIncomeOnly = false;
    private boolean showExpenseOnly = false;
    private boolean isExpenseVisible = true;
    private boolean isIncomeVisible = true;

    private OnSectionClickListener sectionClickListener;

    // Constructors
    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // Initialize Paint object
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    // Set data and calculate percentages for expense and income
    public void setData(float expense, float income) {
        float total = expense + income;
        expensePercentage = (expense / total) * 360;
        incomePercentage = (income / total) * 360;
        invalidate(); // Redraw the view with updated data
    }

    // Show only income
    public void showOnlyIncome() {
        showIncomeOnly = true;
        showExpenseOnly = false;
        invalidate();
    }

    // Show only expense
    public void showOnlyExpense() {
        showIncomeOnly = false;
        showExpenseOnly = true;
        invalidate();
    }
    public void showAllSections() {
        // Logic hiển thị cả Expense và Income
        isExpenseVisible = true;
        isIncomeVisible = true;
        invalidate(); // Làm mới biểu đồ
    }

    // Show both income and expense
    public void showAll() {
        showIncomeOnly = false;
        showExpenseOnly = false;
        invalidate();
    }

    // Set the listener for section click events
    public void setOnSectionClickListener(OnSectionClickListener listener) {
        sectionClickListener = listener;
    }

    // Handle drawing of the pie chart
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float radius = Math.min(centerX, centerY) - 20; // Ensure there is space for the pie chart
        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Draw income and expense based on visibility flags
        if (showIncomeOnly) {
            paint.setColor(getResources().getColor(R.color.green));
            canvas.drawArc(rectF, -90, incomePercentage, true, paint);

            // Draw percentage text
            String percentageText = String.format("%.1f%%", incomePercentage / 360 * 100);
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(percentageText, centerX, centerY, paint);
        } else if (showExpenseOnly) {
            paint.setColor(getResources().getColor(R.color.red));
            canvas.drawArc(rectF, -90, expensePercentage, true, paint);

            // Draw percentage text
            String percentageText = String.format("%.1f%%", expensePercentage / 360 * 100);
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(percentageText, centerX, centerY, paint);
        } else {
            // Draw expense section
            paint.setColor(getResources().getColor(R.color.red));
            canvas.drawArc(rectF, -90, expensePercentage, true, paint);
            expenseStartAngle = -90;

            // Draw income section
            paint.setColor(getResources().getColor(R.color.green));
            canvas.drawArc(rectF, expenseStartAngle, incomePercentage, true, paint);
            incomeStartAngle = expenseStartAngle + expensePercentage;

            // Draw percentage text for Expense
            String expensePercentageText = String.format("%.1f%%", expensePercentage / 360 * 100);
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(expensePercentageText, centerX, centerY - 40, paint);

            // Draw percentage text for Income
            String incomePercentageText = String.format("%.1f%%", incomePercentage / 360 * 100);
            canvas.drawText(incomePercentageText, centerX, centerY + 40, paint);
        }
    }

    // Handle touch events to detect which section of the pie chart was clicked
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            float centerX = getWidth() / 2;
            float centerY = getHeight() / 2;
            float radius = Math.min(centerX, centerY) - 20;

            // Calculate the distance from the touch point to the center of the chart
            double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));

            if (distance <= radius) {
                // Calculate the angle of the touch relative to the center
                float angle = (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));

                if (angle < 0) {
                    angle += 360; // Normalize the angle
                }

                // Check which section was clicked
                if (angle >= 90 && angle <= (90 + expensePercentage)) {
                    if (sectionClickListener != null) {
                        sectionClickListener.onExpenseClick(); // Call expense click listener
                    }
                } else if (angle >= (90 + expensePercentage) && angle <= (90 + expensePercentage + incomePercentage)) {
                    if (sectionClickListener != null) {
                        sectionClickListener.onIncomeClick(); // Call income click listener
                    }
                }
            }
        }
        return true;
    }

    // Interface for click events on pie chart sections
    public interface OnSectionClickListener {
        void onExpenseClick(); // Called when the expense section is clicked
        void onIncomeClick();  // Called when the income section is clicked
    }
}
