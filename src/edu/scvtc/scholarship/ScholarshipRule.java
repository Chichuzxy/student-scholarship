package edu.scvtc.scholarship;

/**
 * 一条奖学金评定规则：某个等级要求「单科不低于多少分」且「操行分不低于多少分」。
 * 规则从代码里抽出来，改分数线只改这里，不动业务逻辑。
 */
public final class ScholarshipRule {

    private final int level;
    private final int minSubjectScore;
    private final int minConductScore;

    public ScholarshipRule(int level, int minSubjectScore, int minConductScore) {
        if (level <= 0) {
            throw new IllegalArgumentException("等级必须大于 0");
        }
        if (minSubjectScore < 0 || minSubjectScore > 100) {
            throw new IllegalArgumentException("单科分数线越界: " + minSubjectScore);
        }
        if (minConductScore < 0 || minConductScore > 100) {
            throw new IllegalArgumentException("操行分数线越界: " + minConductScore);
        }
        this.level = level;
        this.minSubjectScore = minSubjectScore;
        this.minConductScore = minConductScore;
    }

    /** 判定该学生是否满足这一条规则。 */
    public boolean matches(Student student) {
        return student.getMinScore() >= minSubjectScore
                && student.getConductScore() >= minConductScore;
    }

    public int getLevel()            { return level; }
    public int getMinSubjectScore()  { return minSubjectScore; }
    public int getMinConductScore()  { return minConductScore; }

    /** 默认三档：一等单科 90/操行 85，二等 85/85，三等 80/85。 */
    public static ScholarshipRule[] defaultRules() {
        return new ScholarshipRule[] {
            new ScholarshipRule(1, 90, 85),
            new ScholarshipRule(2, 85, 85),
            new ScholarshipRule(3, 80, 85)
        };
    }

    @Override
    public String toString() {
        return level + " 等：单科 >= " + minSubjectScore + "，操行 >= " + minConductScore;
    }
}
