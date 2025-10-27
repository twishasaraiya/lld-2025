package org.example.rippling;

import java.util.ArrayList;
import java.util.List;

/**
 * We have a list of expense and rules. Aim is to validate rules for each expense.
 *
 * Each expense is similar to
 *
 * {
 * 	expenseid: "1"
 * 	itemId: "Item1"
 * 	expensetype: "Food"
 * 	amountInUsd : "250"
 * 	sellerType : "restaurant"
 * 	SellerName "ABC restaurant"
 *
 * }
 * List of rules similar to
 *
 * - Total expense should not be > 175
 * - Seller type restaurant should not have expense more that 45
 * - Entertainment expense type should not be charged
 * Run the rules on expense and flag the rule which do not satisfy. Implement following:
 *
 * evaluateRule(List<rule> , List<expense>)
 */
public class ExpenseDemo {
}

class Expense{
    final String expenseId;
    final String itemId;
    final String expenseType;
    final double amountInUsd;
    final String sellerType;
    final String sellerName;

    public Expense(String expenseId, String itemId, String expenseType, double amountInUsd, String sellerType, String sellerName) {
        this.expenseId = expenseId;
        this.itemId = itemId;
        this.expenseType = expenseType;
        this.amountInUsd = amountInUsd;
        this.sellerType = sellerType;
        this.sellerName = sellerName;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public double getAmountInUsd() {
        return amountInUsd;
    }

    public String getSellerType() {
        return sellerType;
    }

    public String getSellerName() {
        return sellerName;
    }
}
interface Rule{
    String getName();
    boolean violateRule(Expense expense);
}

class TotalExpenseRule implements Rule{
    final double min;
    final double max;

    public TotalExpenseRule(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String getName() {
        return "Total expense should be in range " + min + "," + max;
    }

    @Override
    public boolean violateRule(Expense expense) {
        return expense.getAmountInUsd() < min || expense.getAmountInUsd() > max;
    }
}

class SellerTypeExpenseRule implements Rule{
    final String sellerType;
    final double maxExpense;
    final double minExpense;

    public SellerTypeExpenseRule(String sellerType, double maxExpense, double minExpense) {
        this.sellerType = sellerType;
        this.maxExpense = maxExpense;
        this.minExpense = minExpense;
    }

    @Override
    public String getName() {
        return sellerType + " should have expense in range " + minExpense + " , " + maxExpense;
    }

    @Override
    public boolean violateRule(Expense expense) {
        if(!expense.sellerType.equals(sellerType)) return false;
        return expense.amountInUsd < minExpense || expense.amountInUsd > maxExpense;
    }
}

class ExpenseTypeRule implements Rule{
    final String expenseType;
    final double minExpense;
    final double maxExpense;

    public ExpenseTypeRule(String expenseType, double minExpense, double maxExpense) {
        this.expenseType = expenseType;
        this.minExpense = minExpense;
        this.maxExpense = maxExpense;
    }

    @Override
    public String getName() {
        return expenseType + " expense should be between " + minExpense + " , " + maxExpense;
    }

    @Override
    public boolean violateRule(Expense expense) {
        if(!expense.expenseType.equals(expenseType)) return false;
        return expense.amountInUsd < minExpense || expense.amountInUsd > maxExpense;
    }
}
class ViolationResult{
    Rule rule;
    Expense expense;

    public ViolationResult(Rule rule, Expense expense) {
        this.rule = rule;
        this.expense = expense;
    }
}
interface IExpenseValidator{
    List<ViolationResult> evaluateRule(List<Rule> rules, List<Expense> expenses);
}

class ExpenseValidator implements IExpenseValidator{

    @Override
    public List<ViolationResult> evaluateRule(List<Rule> rules, List<Expense> expenses) {
        List<ViolationResult> violationResults = new ArrayList<>();
        for (Rule rule: rules){
            for(Expense expense: expenses){
                if(!rule.violateRule(expense)){
                    violationResults.add(new ViolationResult(rule, expense));
                }
            }
        }
        return violationResults;
    }
}
