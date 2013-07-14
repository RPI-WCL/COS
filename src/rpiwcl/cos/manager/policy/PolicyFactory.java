package rpiwcl.cos.manager.policy;

import java.text.ParseException;
import java.util.Calendar;
import rpiwcl.cos.manager.policy.*;

public class PolicyFactory {
    private static final String DEADLINE = "deadline";
    private static final String BUDGET = "budget";

    public static Policy getPolicy( String policyStr ) throws ParseException {
        // expecting the following format for policyStr
        //  deadline policy: "deadline={REAL}-TIMEUNIT" (e.g., deadline=30-min)
        //  budget   policy: "budget={REAL}-CURRENCYUNIT"   (e.g., budget=0.5-USD)
        
        Policy policy = null;

        String[] splits = policyStr.split( "=" );
        if ((splits.length != 2) ||
            (!splits[0].equals( DEADLINE ) && !splits[0].equals( BUDGET ))) {
            throw new ParseException( "no valid policy found: ", 0 );
        }

        if (splits[0].contains( DEADLINE )) {
            // parse deadline policy string

            String[] timeSplits = splits[1].split( "-" );
            if (timeSplits.length != 2)
                throw new ParseException( "no valid deadline found: ", 1 );

            double deadline = Double.parseDouble( timeSplits[0] );
            // covert time to seconds
            switch (findTimeUnit( timeSplits[1] )) {
            case Calendar.MILLISECOND:
                deadline /= 1000;
                break;
            case Calendar.SECOND:
                // do nothing
                break;
            case Calendar.MINUTE:
                deadline *= 60;
                break;
            case Calendar.HOUR:
                deadline *= 3600;
                break;
            }

            System.out.println( "[PoliFactory] deadline=" + splits[1] + "(" + deadline + "s)" );
            
            policy = new DeadlinePolicy( deadline );
        }
        else if (policyStr.contains( BUDGET )) {
            // parse budget policy string

            String[] budgetSplits = splits[1].split( "-" );
            if (budgetSplits.length != 2)
                throw new ParseException( "no valid deadline found: ", 1 );
            if (!budgetSplits[1].equalsIgnoreCase( "USD" ))
                throw new ParseException( "currently only USD is supported: ", 2 );
            double budget = Double.parseDouble( budgetSplits[0] );

            System.out.println( "[PoliFactory] budget=" + splits[1] + "(" + budget + "USD)" );

            policy = new BudgetPolicy( budget );
        }
        else {
            throw new ParseException( "no valid policy found: ", 0 );
        }

        return policy;
    }

    private static int findTimeUnit( String timeStr ) {
        // returns a Calendar field
        String[] units = {"msec", "ms", "sec", "s", "min", "m", "hour", "hr", "h"};

        boolean found = false;
        int i;
        for (i = 0; i < units.length; i++) {
            if (timeStr.equalsIgnoreCase( units[i] )) {
                found = true;
                break;
            }
        }

        int field = -1;
        if (found) {
            if (i < 2)
                field = Calendar.MILLISECOND;
            if (i < 4)
                field = Calendar.SECOND;
            else if (i < 6)
                field = Calendar.MINUTE;
            else 
                field = Calendar.HOUR;
        }

        return field;
    }

}
