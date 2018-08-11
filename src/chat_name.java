import java.util.*;

public class chat_name {
    public static void main(String[] args) {
        List<String> list = new chat_name().result();
        for (String x : list)
            System.out.println(x);
    }
    public List<String> result() {
        Scanner scanner = new Scanner(System.in);
        int num = Integer.parseInt(scanner.nextLine());
        List<String> result = new ArrayList<String>();
        HashMap<String, String> members = new HashMap<String, String>(num);
        while (num>0) {
            String[] msg1 = scanner.nextLine().split(" ");
            members.put(msg1[1], msg1[0]);
            result.add(msg1[1]);
            num--;
        }
        int num2 = Integer.parseInt(scanner.nextLine());;
        HashMap<String, String> status = new HashMap<String, String>();
        while (num2>0) {
            String[] msg2 = scanner.nextLine().split(" ");
            status.put(msg2[0], msg2[1]);
            num2--;
        }

        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (status.get(o1).equals("1") && status.get(o2).equals("0")){
                    return -1;
                } else if (status.get(o1).equals(status.get(o2))) {
                    return Integer.parseInt(members.get(o2)) - Integer.parseInt(members.get(o1));
                } else
                    return 1;
            }
        };
        Collections.sort(result, comparator);
        
//        Collections.reverse(result);
        return result;
    }

}
