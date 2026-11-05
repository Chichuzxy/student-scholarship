package edu.scvtc.scholarship;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 命令行入口：演示 录入 -> 排序 -> 评定 -> 输出 的完整流程，
 * 同时把结果写入 report.txt（UTF-8），方便贴到文档或提交记录里。
 */
public class Main {

    public static void main(String[] args) throws Exception {
        StudentService service = demoService();
        ScholarshipRule[] rules = ScholarshipRule.defaultRules();

        StringBuilder sb = new StringBuilder();
        sb.append("待评定学生（录入顺序）\n");
        sb.append(header());
        for (Student s : service.all()) {
            sb.append(row(s));
        }

        sb.append("\n按总分降序排序（总分相同按学号升序）\n");
        sb.append(header());
        for (Student s : service.sortByTotalDesc()) {
            sb.append(row(s));
        }

        int[] counts = service.evaluateScholarship(rules);
        sb.append("\n奖学金评定结果\n");
        for (int i = 0; i < rules.length; i++) {
            sb.append(rules[i].toString()).append("，共 ").append(counts[i]).append(" 人\n");
        }
        for (int level = 1; level <= rules.length; level++) {
            List<Student> list = service.byLevel(level);
            if (list.isEmpty()) {
                sb.append("  ").append(level).append(" 等：无\n");
                continue;
            }
            for (Student s : list) {
                sb.append("  ").append(s.toString()).append("\n");
            }
        }

        sb.append("\n统计\n");
        sb.append(String.format("  平均分：%.2f%n", service.averageOfAll()));
        sb.append(String.format("  全部科目及格率：%.1f%%%n", service.passRate() * 100));
        sb.append("  学生总数：").append(service.size()).append("\n");

        System.out.println(sb);
        try (PrintStream out = new PrintStream(new FileOutputStream("report.txt"), true, "UTF-8")) {
            out.print(sb);
        }
        System.out.println("结果已写入 report.txt (" + StandardCharsets.UTF_8.name() + ")");
    }

    private static String header() {
        return "学号\t姓名\t成绩\t\t\t操行\t总分\t奖学金\n";
    }

    private static String row(Student s) {
        return s.toString() + "\n";
    }

    private static StudentService demoService() {
        StudentService service = new StudentService();
        service.add(new Student("24区块链2", "01", "张晓燚", new int[]{98, 100, 96, 95, 89}, 85));
        service.add(new Student("24区块链2", "02", "李四",   new int[]{79, 77, 78, 86, 80}, 86));
        service.add(new Student("24区块链2", "03", "张三",   new int[]{56, 68, 70, 72, 65}, 86));
        service.add(new Student("24区块链2", "04", "王五",   new int[]{86, 90, 93, 85, 85}, 86));
        service.add(new Student("24区块链2", "05", "李明",   new int[]{84, 85, 88, 85, 89}, 86));
        service.add(new Student("24区块链2", "06", "罗星",   new int[]{98, 90, 96, 95, 93}, 86));
        service.add(new Student("24区块链2", "07", "罗光明", new int[]{85, 86, 88, 86, 89}, 86));
        service.add(new Student("24区块链2", "08", "李浩然", new int[]{86, 90, 95, 90, 95}, 89));
        service.add(new Student("24区块链2", "09", "彭冬",   new int[]{86, 86, 83, 75, 75}, 86));
        service.add(new Student("24区块链2", "10", "刘星",   new int[]{94, 95, 68, 85, 82}, 89));
        return service;
    }
}
