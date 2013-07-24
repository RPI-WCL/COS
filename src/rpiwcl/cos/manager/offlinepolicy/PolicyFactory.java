package rpiwcl.cos.manager.offlinepolicy;

public class PolicyFactory {

    public static Policy getPolicy( String type, double constraint, String option ) {
        Policy policy = null;

        switch (type) {
        case "deadline":
            policy = new DeadlinePolicy( constraint, option );
            break;
        case "budget":
            policy = new BudgetPolicy( constraint, option );
            break;
        default:
            System.err.println( "[PolicyFactory] ERROR undefined policy type: " + type );
            break;
        }

        return policy;
    }

}

        
    
