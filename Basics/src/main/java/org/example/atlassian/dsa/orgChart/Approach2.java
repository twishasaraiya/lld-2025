package org.example.atlassian.dsa.orgChart;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Memory efficient compared to approach 1 since it needs to store O(m*n) path for all employess
 */
public class Approach2 {
    public static void main(String[] args) {
        OrgManager org = new OrgManager();
        org.addEmployee("ceo", null, List.of());
        org.addEmployee("vp1", "ceo", List.of());
        org.addEmployee("vp2", "ceo", List.of());
        org.addEmployee("m1", "vp1", List.of());
        org.addEmployee("m2", "vp1", List.of());
        org.addEmployee("m12", "m1", List.of());
        org.addEmployee("m121", "m12", List.of());

        System.out.println(org.findParent(List.of("vp1", "vp2")).name);
        System.out.println(org.findParent(List.of("vp1", "m1")).name);
        System.out.println(org.findParent(List.of("m121", "m2")).name);

    }

    static class Employee {
        String name;
        Employee manager;
        List<Employee> reports;

        public Employee(String name, Employee manager, List<Employee> reports){
            this.name = name;
            this.manager = manager;
            this.reports = reports;
        }
    }

    static class OrgManager {
        private Map<String, Employee> employeeMap;
        private Map<Employee, Employee> childToParentMap;

        public OrgManager() {
            this.employeeMap = new HashMap<>();
            this.childToParentMap = new HashMap<>();
        }

        private void addEmployee(String name, String managerName, List<String> reports){
            if(name == null || reports == null){
                throw new RuntimeException("Invalid input params");
            }
            if(employeeMap.containsKey(name)){
                throw new RuntimeException("Employee with same name already exists");
            }

            Employee manager = employeeMap.get(managerName);
            List<Employee> directReports = reports.stream().map(e -> employeeMap.get(e)).collect(Collectors.toList());
            Employee employee = new Employee(name, manager, directReports);
            employeeMap.put(name, employee);
            if (manager != null) manager.reports.add(employee);
        }

        public Employee findParent(List<String> employeeNames){
            List<Employee> employees = employeeNames.stream().map(e -> employeeMap.get(e)).collect(Collectors.toList());

            Employee lca = employees.get(0);
            for (int i = 1; i < employees.size(); i++) {
                lca = findCommonParent(lca, employees.get(i));
            }

            return lca;
        }

        private Employee findCommonParent(Employee emp1, Employee emp2){

            HashSet<Employee> pathToRoot = new HashSet<>();
            while (emp1 !=null){
                pathToRoot.add(emp1);
                emp1 = emp1.manager;
            }

            while (emp2 != null){
                if(pathToRoot.contains(emp2)){
                    return emp2; // found lca
                }
                emp2 = emp2.manager;
            }
            return null;
        }
    }

}
