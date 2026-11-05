package edu.scvtc.scholarship;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * 命令行入口：演示 录入 -> 排序 -> 评定 -> 输出 的完整流程，
 * 同时把结果写入 report.txt（UTF-8），方便贴到文档或提交记录里。
 */
public class Main {

    private static final int W_NO = 6;        // 学号
    private static final int W_NAME = 10;     // 姓名
    private static final int W_SCORES = 26;   // 五门成绩
    private static final int W_CONDUCT = 8;   // 操行
    private static final int W_TOTAL = 8;     // 总分
    private static final int W_LEVEL = 8;     // 奖学金

    public static void main(String[] args) throws Exception {
        StudentService service = demoService();
        ScholarshipRule[] rules = ScholarshipRule.defaultRules();

        StringBuilder sb = new StringBuilder();
        sb.append("待评定学生（录入顺序）\n");
        sb.append(tableHeader());
        for (Student s : service.all()) {
            sb.append(tableRow(s, false));
        }

        sb.append("\n按总分降序排序（总分相同按学号升序）\n");
        sb.append(tableHeader());
        for (Student s : service.sortByTotalDesc()) {
            sb.append(tableRow(s, false));
        }

        int[] counts = service.evaluateScholarship(rules);
        sb.append("\n奖学金评定结果\n");
        for (int i = 0; i < rules.length; i++) {
            sb.append(rules[i].toString()).append("，共 ").append(counts[i]).append(" 人\n");
        }
        sb.append("\n");
        sb.append(tableHeader());
        boolean any = false;
        for (Student s : service.sortByTotalDesc()) {
            if (s.getScholarshipLevel() > 0) {
                sb.append(tableRow(s, true));
                any = true;
            }
        }
        if (!any) {
            sb.append("  本次无学生获得奖学金\n");
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

    private static String tableHeader() {
        String head = pad("学号", W_NO) + pad("姓名", W_NAME) + pad("成绩", W_SCORES)
                + pad("操行", W_CONDUCT) + pad("总分", W_TOTAL) + pad("奖学金", W_LEVEL);
        String line = repeat('-', W_NO) + "  " + repeat('-', W_NAME) + "  " + repeat('-', W_SCORES)
                + "  " + repeat('-', W_CONDUCT) + "  " + repeat('-', W_TOTAL) + "  " + repeat('-', W_LEVEL);
        return "  " + head + "\n  " + line + "\n";
    }

    /** withLevel 为 true 时最后一列显示实际等级，否则显示"无"。 */
    private static String tableRow(Student s, boolean withLevel) {
        String level = withLevel
                ? (s.getScholarshipLevel() == 0 ? "无" : s.getScholarshipLevel() + " 等")
                : "无";
        return "  " + pad(s.getStudentNo(), W_NO)
                + pad(s.getName(), W_NAME)
                + pad(joinScores(s.getScores()), W_SCORES)
                + pad(String.valueOf(s.getConductScore()), W_CONDUCT)
                + pad(String.valueOf(s.getTotalScore()), W_TOTAL)
                + pad(level, W_LEVEL)
                + "\n";
    }

    private static String joinScores(int[] scores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scores.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(scores[i]);
        }
        return sb.toString();
    }

    /** 按显示宽度左对齐填充，并在列后补两格间隔。 */
    private static String pad(String text, int width) {
        StringBuilder sb = new StringBuilder(text);
        for (int i = displayWidth(text); i < width; i++) {
            sb.append(' ');
        }
        return sb.append("  ").toString();
    }

    /** 计算显示宽度：汉字等全角字符占 2 列。 */
    private static int displayWidth(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            width += isFullWidth(text.charAt(i)) ? 2 : 1;
        }
        return width;
    }

    private static boolean isFullWidth(char c) {
        return (c >= 0x1100 && c <= 0x115F)
                || (c >= 0x2E80 && c <= 0xA4CF)
                || (c >= 0xAC00 && c <= 0xD7A3)
                || (c >= 0xF900 && c <= 0xFAFF)
                || (c >= 0xFE30 && c <= 0xFE6F)
                || (c >= 0xFF00 && c <= 0xFF60)
                || (c >= 0xFFE0 && c <= 0xFFE6);
    }

    private static String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
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
