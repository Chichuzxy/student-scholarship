package edu.scvtc.scholarship;

import java.util.Arrays;

/**
 * 学生实体。
 * 只负责保存数据与派生计算（总分、最低单科、平均分），不参与排序和评定决策，
 * 这样规则变化时不用改这个类。
 */
public class Student {

    private final String classNo;
    private final String studentNo;
    private final String name;
    private final int[] scores;
    private final int conductScore;
    private int scholarshipLevel;   // 0 = 未获得

    public Student(String classNo, String studentNo, String name, int[] scores, int conductScore) {
        if (studentNo == null || studentNo.trim().isEmpty()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        if (scores == null || scores.length == 0) {
            throw new IllegalArgumentException("成绩不能为空");
        }
        for (int s : scores) {
            if (s < 0 || s > 100) {
                throw new IllegalArgumentException("成绩必须在 0-100 之间，异常值: " + s);
            }
        }
        if (conductScore < 0 || conductScore > 100) {
            throw new IllegalArgumentException("操行分必须在 0-100 之间，异常值: " + conductScore);
        }
        this.classNo = classNo;
        this.studentNo = studentNo;
        this.name = name;
        this.scores = Arrays.copyOf(scores, scores.length);
        this.conductScore = conductScore;
    }

    public int getTotalScore() {
        int sum = 0;
        for (int s : scores) {
            sum += s;
        }
        return sum;
    }

    public int getMinScore() {
        int min = Integer.MAX_VALUE;
        for (int s : scores) {
            if (s < min) {
                min = s;
            }
        }
        return min;
    }

    public double getAverageScore() {
        return (double) getTotalScore() / scores.length;
    }

    public boolean isAllPassed() {
        return getMinScore() >= 60;
    }

    public String getClassNo()          { return classNo; }
    public String getStudentNo()        { return studentNo; }
    public String getName()             { return name; }
    public int[] getScores()            { return Arrays.copyOf(scores, scores.length); }
    public int getScoreCount()          { return scores.length; }
    public int getConductScore()        { return conductScore; }
    public int getScholarshipLevel()    { return scholarshipLevel; }
    public void setScholarshipLevel(int level) { this.scholarshipLevel = level; }

    @Override
    public String toString() {
        return studentNo + "\t" + name + "\t" + Arrays.toString(scores)
                + "\t操行" + conductScore + "\t总分" + getTotalScore()
                + "\t" + (scholarshipLevel == 0 ? "无" : scholarshipLevel + " 等");
    }
}
