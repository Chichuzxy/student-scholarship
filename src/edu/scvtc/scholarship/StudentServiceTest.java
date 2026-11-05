package edu.scvtc.scholarship;

import java.util.List;

/**
 * 不依赖 JUnit 的自测入口（零外部依赖，javac + java 即可跑）。
 * 覆盖：计算、排序稳定性、评定边界、重复评定、异常入参、空集合。
 */
public class StudentServiceTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        // --- 基础计算 ---
        Student a = new Student("C1", "01", "A", new int[]{90, 91, 92, 93, 94}, 90);
        check("total score = sum of subjects", a.getTotalScore() == 460);
        check("average score", Math.abs(a.getAverageScore() - 92.0) < 1e-9);
        check("min subject picked", a.getMinScore() == 90);
        check("all passed when min >= 60", a.isAllPassed());

        Student fail = new Student("C1", "02", "B", new int[]{100, 100, 59, 100, 100}, 90);
        check("one subject below 60 -> not all passed", !fail.isAllPassed());

        // --- 排序 ---
        StudentService svc = new StudentService();
        svc.add(new Student("C1", "03", "C", new int[]{80, 80, 80, 80, 80}, 90)); // 400
        svc.add(new Student("C1", "01", "D", new int[]{90, 90, 90, 90, 90}, 90)); // 450
        svc.add(new Student("C1", "02", "E", new int[]{80, 80, 80, 80, 80}, 90)); // 400
        List<Student> sorted = svc.sortByTotalDesc();
        check("sort: highest total first", sorted.get(0).getStudentNo().equals("01"));
        check("sort: tie broken by studentNo asc", sorted.get(1).getStudentNo().equals("02")
                && sorted.get(2).getStudentNo().equals("03"));

        // --- 奖学金评定边界 ---
        StudentService svc2 = new StudentService();
        svc2.add(new Student("C1", "01", "L1", new int[]{90, 90, 90, 90, 90}, 85)); // 一等
        svc2.add(new Student("C1", "02", "L2", new int[]{89, 95, 95, 95, 95}, 85)); // 单科 89 -> 二等
        svc2.add(new Student("C1", "03", "L3", new int[]{80, 80, 80, 80, 80}, 84)); // 操行不足 -> 无
        svc2.add(new Student("C1", "04", "L0", new int[]{70, 70, 70, 70, 70}, 95)); // 单科不足 -> 无
        svc2.add(new Student("C1", "05", "L1B", new int[]{95, 95, 95, 95, 95}, 99)); // 一等
        int[] counts = svc2.evaluateScholarship(ScholarshipRule.defaultRules());
        check("level1 count = 2", counts[0] == 2);
        check("level2 count = 1", counts[1] == 1);
        check("level3 count = 0", counts[2] == 0);
        check("conduct below threshold gets nothing", svc2.findByNo("03").getScholarshipLevel() == 0);
        check("subject below threshold gets nothing", svc2.findByNo("04").getScholarshipLevel() == 0);
        check("one student evaluated only once", svc2.byLevel(1).size() + svc2.byLevel(2).size()
                + svc2.byLevel(3).size() == 3);

        // --- 重算时清零 ---
        svc2.evaluateScholarship(ScholarshipRule.defaultRules());
        check("re-evaluate resets previous level", svc2.byLevel(1).size() == 2);

        // --- 空集合 ---
        StudentService empty = new StudentService();
        check("empty: average = 0", empty.averageOfAll() == 0.0);
        check("empty: pass rate = 0", empty.passRate() == 0.0);
        check("empty: sort returns empty list", empty.sortByTotalDesc().isEmpty());

        // --- 入参校验 ---
        check("reject score > 100", throwsOn(() -> new Student("C1", "09", "X", new int[]{101}, 90)));
        check("reject score < 0", throwsOn(() -> new Student("C1", "09", "X", new int[]{-1}, 90)));
        check("reject blank studentNo", throwsOn(() -> new Student("C1", "  ", "X", new int[]{90}, 90)));
        check("reject null scores", throwsOn(() -> new Student("C1", "09", "X", null, 90)));

        System.out.println("\n" + passed + " passed / " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private interface Block {
        void run();
    }

    private static boolean throwsOn(Block block) {
        try {
            block.run();
            return false;
        } catch (RuntimeException e) {
            return true;
        }
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }
}
