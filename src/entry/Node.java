package entry;

public class Node {
    public static final double FULLENERGY = 100;
    private double[] location;
    private double remainEnergy;
    private int requestTime;
    private boolean isResponse;


    private double Weight;

    public Node(double[] location, double remainEnergy) {
        this.location = location;
        this.remainEnergy = remainEnergy;
    }

    //  充电，返回充电量
    public double charge() {
        double chargeEnergy = FULLENERGY-this.remainEnergy;
        this.remainEnergy = FULLENERGY;
        return chargeEnergy;
    }

    public double[] getLocation() {
        return location;
    }
    public double getRemainEnergy() {
        return remainEnergy;
    }
    public void setRemainEnergy(double remainEnergy) {
        this.remainEnergy = remainEnergy;
    }
    public boolean isResponse() {
        return isResponse;
    }

    public void setResponse(boolean response) {
        isResponse = response;
    }

    public boolean getResponse() {
        return isResponse;
    }

    public int getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(int requestTime) {
        this.requestTime = requestTime;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }
}
