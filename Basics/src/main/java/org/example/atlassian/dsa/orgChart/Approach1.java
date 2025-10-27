package org.example.atlassian.dsa.orgChart;

import java.util.*;
import java.util.stream.Collectors;

public class Approach1 {

    public static void main(String[] args) {
       OrgChart orgChart = new OrgChart();
       orgChart.addEmployee("ceo",null, List.of(), true);
       orgChart.addEmployee("vp1", "ceo", List.of(), false);
       orgChart.addEmployee("vp2", "ceo", List.of(), false);
       orgChart.addEmployee("m1", "vp1", List.of(),false);
       orgChart.addEmployee("dev1", "m1", List.of(), false);
       orgChart.addEmployee("m2", "vp1", List.of(), false);

       Employee lca = orgChart.getLowestCommonManager(List.of("m1", "dev1"));
        System.out.println(lca.name);

        lca = orgChart.getLowestCommonManager(List.of("m1", "dev1", "m2"));
        System.out.println(lca.name);

        lca = orgChart.getLowestCommonManager(List.of("m1", "dev4"));
        System.out.println(lca.name);
    }

    static class Employee {
        String id;
        String name;
        Employee manager;
        List<Employee> reports;

        public Employee(String name, Employee manager, List<Employee> reports) {
            this.name = name;
            this.manager = manager;
            this.reports = reports;
        }

        public Employee(String name) {
            this.name = name;
            this.manager = null;
            this.reports = new ArrayList<>();
        }
    }

    static class OrgChart {
        Employee root;
        Map<String, Employee> employeeMap;

        public OrgChart() {
            this.root = null;
            this.employeeMap = new HashMap<>();
        }

        public void addEmployee(String name, String manager, List<String> reports, Boolean isCeo) {
            if (employeeMap.containsKey(name)) {
                throw new RuntimeException("Same name employee already exists");
            }

            Employee m = employeeMap.get(manager);
            List<Employee> reportsList = reports.stream()
                    .map(r -> employeeMap.getOrDefault(r, null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            Employee employee = new Employee(name, m, reportsList);
            employeeMap.put(name, employee);
            if (isCeo) {
                root = employee;
            } else {
                m.reports.add(employee);
            }
        }

        public Employee getLowestCommonManager(List<String> employees) {
            List<List<Employee>> paths = employees.stream()
                    .map(e -> getPathToRoot(employeeMap.get(e)))
                    .collect(Collectors.toList());

            int level = 0;
            Employee candidate = null, lca = null;
            boolean isSameManager = true;
            while (isSameManager) {
                int pos = paths.get(0).size() - 1 - level;
                if (pos < 0) break;
                candidate = paths.get(0).get(pos);
                for (List<Employee> path : paths) {
                    pos = path.size() - 1 - level;
                    if (pos < 0 || !path.get(pos).equals(candidate)) {
                        isSameManager = false;
                        break;
                    }
                }
                if (!isSameManager) break;
                level++;
                lca = candidate;
            }

            return lca;

        }

        private List<Employee> getPathToRoot(Employee emp) {
            if (emp == null) throw new RuntimeException("Employee is not valid");
            List<Employee> path = new ArrayList<>();
            Employee og = emp;
            while (emp != null) {
                path.add(emp);
                emp = emp.manager;
            }

            System.out.println(og.name + " -> path -> " + path);
            return path;
        }
    }
}


