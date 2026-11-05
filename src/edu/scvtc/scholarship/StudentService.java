package edu.scvtc.scholarship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 学生成绩业务：录入、排序、奖学金评定、统计。
 * 不接触控制台输入输出，方便单元测试直接调用。
 */
public class StudentService {

    private final List<Student> students = new ArrayList<>();

    public void add(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("学生对象不能为 null");
        }
        students.add(student);
    }

    public void addAll(List<Student> list) {
        for (Student s : list) {
            add(s);
        }
    }

    public List<Student> all() {
        return Collections.unmodifiableList(students);
    }

    public int size() {
        return students.size();
    }

    /**
     * 按总分降序排序；总分相同按学号升序，保证结果稳定可复现。
     */
    public List<Student> sortByTotalDesc() {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator
                .comparingInt(Student::getTotalScore).reversed()
                .thenComparing(Student::getStudentNo));
        return sorted;
    }

    /**
     * 按等级顺序评定奖学金。
     * 每个学生只评一次：命中高等级后不再参与低等级评定。
     *
     * @return 各等级获奖人数，下标 0 对应一等
     */
    public int[] evaluateScholarship(ScholarshipRule[] rules) {
        for (Student s : students) {
            s.setScholarshipLevel(0);
        }
        int[] counts = new int[rules.length];
        for (Student s : students) {
            for (int i = 0; i < rules.length; i++) {
                if (s.getScholarshipLevel() == 0 && rules[i].matches(s)) {
                    s.setScholarshipLevel(rules[i].getLevel());
                    counts[i]++;
                    break;
                }
            }
        }
        return counts;
    }

    public List<Student> byLevel(int level) {
        List<Student> result = new ArrayList<>();
        for (Student s : students) {
            if (s.getScholarshipLevel() == level) {
                result.add(s);
            }
        }
        return result;
    }

    public double averageOfAll() {
        if (students.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        int count = 0;
        for (Student s : students) {
            total += s.getTotalScore();
            count += s.getScoreCount();
        }
        return (double) total / count;
    }

    /** 及格率：所有科目全部及格的学生占比。 */
    public double passRate() {
        if (students.isEmpty()) {
            return 0.0;
        }
        int passed = 0;
        for (Student s : students) {
            if (s.isAllPassed()) {
                passed++;
            }
        }
        return (double) passed / students.size();
    }

    public Student findByNo(String studentNo) {
        for (Student s : students) {
            if (s.getStudentNo().equals(studentNo)) {
                return s;
            }
        }
        return null;
    }
}
