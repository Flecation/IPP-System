package IPPSystem.Models;

public class ReportLabor {
    private String name;
    private String skill;
    private double wage;
    private double hours;
    private double total;

    // Constructor
    public ReportLabor(String name, String skill, double wage, double hours, double total) {
        this.name = name;
        this.skill = skill;
        this.wage = wage;
        this.hours = hours;
        this.total = total;
    }

    // Getters (Table Column တွေက ဒါတွေကို လှမ်းခေါ်မှာပါ)
    public String getName() { return name; }
    public String getSkill() { return skill; }
    public double getWage() { return wage; }
    public double getHours() { return hours; }
    public double getTotal() { return total; }
}