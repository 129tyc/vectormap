package com.tyc129.vectormap.struct;

/**
 * 兴趣热点
 * 代表地图上的商户、建筑、重要位置等
 * Created by Code on 2017/5/15 0015.
 *
 * @author 谈永成
 * @version 1.0
 */
public class Interest extends Point {
    public enum InterestType {
        BUILDING,
        TOILET,
        ENTRANCE,
        EXIT,
        LANDSCAPE,
        RESTAURANT,
        DEFAULT
    }

    public enum ImportantLevel {
        USERSET,
        MOST,
        USUALLY,
        NORMAL,
        LOW,
    }

    private InterestType type;
    private ImportantLevel level;
    private String innerId;

    public Interest(String id) {
        super(id);
    }

    public Interest(String id, Coordinate coordinate) {
        super(id, coordinate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Interest interest = (Interest) o;

        return type == interest.type &&
                level == interest.level &&
                (innerId != null ? innerId.equals(interest.innerId) : interest.innerId == null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (innerId != null ? innerId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Interest{" +
                "type=" + type +
                ", level=" + level +
                ", innerId='" + innerId + '\'' +
                '}';
    }

    public InterestType getType() {
        return type;
    }

    public void setType(InterestType type) {
        this.type = type;
    }

    public ImportantLevel getLevel() {
        return level;
    }

    public void setLevel(ImportantLevel level) {
        this.level = level;
    }

    public String getInnerId() {
        return innerId;
    }

    public void setInnerId(String innerId) {
        this.innerId = innerId;
    }
}
