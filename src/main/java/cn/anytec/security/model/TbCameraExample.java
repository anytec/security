package cn.anytec.security.model;

import java.util.ArrayList;
import java.util.List;

public class TbCameraExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TbCameraExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andSdkIdIsNull() {
            addCriterion("sdk_id is null");
            return (Criteria) this;
        }

        public Criteria andSdkIdIsNotNull() {
            addCriterion("sdk_id is not null");
            return (Criteria) this;
        }

        public Criteria andSdkIdEqualTo(String value) {
            addCriterion("sdk_id =", value, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdNotEqualTo(String value) {
            addCriterion("sdk_id <>", value, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdGreaterThan(String value) {
            addCriterion("sdk_id >", value, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdGreaterThanOrEqualTo(String value) {
            addCriterion("sdk_id >=", value, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdLessThan(String value) {
            addCriterion("sdk_id <", value, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdLessThanOrEqualTo(String value) {
            addCriterion("sdk_id <=", value, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdLike(String value) {
            addCriterion("sdk_id like", value, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdNotLike(String value) {
            addCriterion("sdk_id not like", value, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdIn(List<String> values) {
            addCriterion("sdk_id in", values, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdNotIn(List<String> values) {
            addCriterion("sdk_id not in", values, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdBetween(String value1, String value2) {
            addCriterion("sdk_id between", value1, value2, "sdkId");
            return (Criteria) this;
        }

        public Criteria andSdkIdNotBetween(String value1, String value2) {
            addCriterion("sdk_id not between", value1, value2, "sdkId");
            return (Criteria) this;
        }

        public Criteria andStreamAddressIsNull() {
            addCriterion("stream_address is null");
            return (Criteria) this;
        }

        public Criteria andStreamAddressIsNotNull() {
            addCriterion("stream_address is not null");
            return (Criteria) this;
        }

        public Criteria andStreamAddressEqualTo(String value) {
            addCriterion("stream_address =", value, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressNotEqualTo(String value) {
            addCriterion("stream_address <>", value, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressGreaterThan(String value) {
            addCriterion("stream_address >", value, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressGreaterThanOrEqualTo(String value) {
            addCriterion("stream_address >=", value, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressLessThan(String value) {
            addCriterion("stream_address <", value, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressLessThanOrEqualTo(String value) {
            addCriterion("stream_address <=", value, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressLike(String value) {
            addCriterion("stream_address like", value, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressNotLike(String value) {
            addCriterion("stream_address not like", value, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressIn(List<String> values) {
            addCriterion("stream_address in", values, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressNotIn(List<String> values) {
            addCriterion("stream_address not in", values, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressBetween(String value1, String value2) {
            addCriterion("stream_address between", value1, value2, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andStreamAddressNotBetween(String value1, String value2) {
            addCriterion("stream_address not between", value1, value2, "streamAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressIsNull() {
            addCriterion("play_address is null");
            return (Criteria) this;
        }

        public Criteria andPlayAddressIsNotNull() {
            addCriterion("play_address is not null");
            return (Criteria) this;
        }

        public Criteria andPlayAddressEqualTo(String value) {
            addCriterion("play_address =", value, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressNotEqualTo(String value) {
            addCriterion("play_address <>", value, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressGreaterThan(String value) {
            addCriterion("play_address >", value, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressGreaterThanOrEqualTo(String value) {
            addCriterion("play_address >=", value, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressLessThan(String value) {
            addCriterion("play_address <", value, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressLessThanOrEqualTo(String value) {
            addCriterion("play_address <=", value, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressLike(String value) {
            addCriterion("play_address like", value, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressNotLike(String value) {
            addCriterion("play_address not like", value, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressIn(List<String> values) {
            addCriterion("play_address in", values, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressNotIn(List<String> values) {
            addCriterion("play_address not in", values, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressBetween(String value1, String value2) {
            addCriterion("play_address between", value1, value2, "playAddress");
            return (Criteria) this;
        }

        public Criteria andPlayAddressNotBetween(String value1, String value2) {
            addCriterion("play_address not between", value1, value2, "playAddress");
            return (Criteria) this;
        }

        public Criteria andCameraStatusIsNull() {
            addCriterion("camera_status is null");
            return (Criteria) this;
        }

        public Criteria andCameraStatusIsNotNull() {
            addCriterion("camera_status is not null");
            return (Criteria) this;
        }

        public Criteria andCameraStatusEqualTo(Integer value) {
            addCriterion("camera_status =", value, "cameraStatus");
            return (Criteria) this;
        }

        public Criteria andCameraStatusNotEqualTo(Integer value) {
            addCriterion("camera_status <>", value, "cameraStatus");
            return (Criteria) this;
        }

        public Criteria andCameraStatusGreaterThan(Integer value) {
            addCriterion("camera_status >", value, "cameraStatus");
            return (Criteria) this;
        }

        public Criteria andCameraStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("camera_status >=", value, "cameraStatus");
            return (Criteria) this;
        }

        public Criteria andCameraStatusLessThan(Integer value) {
            addCriterion("camera_status <", value, "cameraStatus");
            return (Criteria) this;
        }

        public Criteria andCameraStatusLessThanOrEqualTo(Integer value) {
            addCriterion("camera_status <=", value, "cameraStatus");
            return (Criteria) this;
        }

        public Criteria andCameraStatusIn(List<Integer> values) {
            addCriterion("camera_status in", values, "cameraStatus");
            return (Criteria) this;
        }

        public Criteria andCameraStatusNotIn(List<Integer> values) {
            addCriterion("camera_status not in", values, "cameraStatus");
            return (Criteria) this;
        }

        public Criteria andCameraStatusBetween(Integer value1, Integer value2) {
            addCriterion("camera_status between", value1, value2, "cameraStatus");
            return (Criteria) this;
        }

        public Criteria andCameraStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("camera_status not between", value1, value2, "cameraStatus");
            return (Criteria) this;
        }

        public Criteria andCameraTypeIsNull() {
            addCriterion("camera_type is null");
            return (Criteria) this;
        }

        public Criteria andCameraTypeIsNotNull() {
            addCriterion("camera_type is not null");
            return (Criteria) this;
        }

        public Criteria andCameraTypeEqualTo(String value) {
            addCriterion("camera_type =", value, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeNotEqualTo(String value) {
            addCriterion("camera_type <>", value, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeGreaterThan(String value) {
            addCriterion("camera_type >", value, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeGreaterThanOrEqualTo(String value) {
            addCriterion("camera_type >=", value, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeLessThan(String value) {
            addCriterion("camera_type <", value, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeLessThanOrEqualTo(String value) {
            addCriterion("camera_type <=", value, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeLike(String value) {
            addCriterion("camera_type like", value, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeNotLike(String value) {
            addCriterion("camera_type not like", value, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeIn(List<String> values) {
            addCriterion("camera_type in", values, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeNotIn(List<String> values) {
            addCriterion("camera_type not in", values, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeBetween(String value1, String value2) {
            addCriterion("camera_type between", value1, value2, "cameraType");
            return (Criteria) this;
        }

        public Criteria andCameraTypeNotBetween(String value1, String value2) {
            addCriterion("camera_type not between", value1, value2, "cameraType");
            return (Criteria) this;
        }

        public Criteria andServerLabelIsNull() {
            addCriterion("server_label is null");
            return (Criteria) this;
        }

        public Criteria andServerLabelIsNotNull() {
            addCriterion("server_label is not null");
            return (Criteria) this;
        }

        public Criteria andServerLabelEqualTo(String value) {
            addCriterion("server_label =", value, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelNotEqualTo(String value) {
            addCriterion("server_label <>", value, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelGreaterThan(String value) {
            addCriterion("server_label >", value, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelGreaterThanOrEqualTo(String value) {
            addCriterion("server_label >=", value, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelLessThan(String value) {
            addCriterion("server_label <", value, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelLessThanOrEqualTo(String value) {
            addCriterion("server_label <=", value, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelLike(String value) {
            addCriterion("server_label like", value, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelNotLike(String value) {
            addCriterion("server_label not like", value, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelIn(List<String> values) {
            addCriterion("server_label in", values, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelNotIn(List<String> values) {
            addCriterion("server_label not in", values, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelBetween(String value1, String value2) {
            addCriterion("server_label between", value1, value2, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andServerLabelNotBetween(String value1, String value2) {
            addCriterion("server_label not between", value1, value2, "serverLabel");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andGroupIdIsNull() {
            addCriterion("group_id is null");
            return (Criteria) this;
        }

        public Criteria andGroupIdIsNotNull() {
            addCriterion("group_id is not null");
            return (Criteria) this;
        }

        public Criteria andGroupIdEqualTo(Integer value) {
            addCriterion("group_id =", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotEqualTo(Integer value) {
            addCriterion("group_id <>", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdGreaterThan(Integer value) {
            addCriterion("group_id >", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("group_id >=", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLessThan(Integer value) {
            addCriterion("group_id <", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLessThanOrEqualTo(Integer value) {
            addCriterion("group_id <=", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdIn(List<Integer> values) {
            addCriterion("group_id in", values, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotIn(List<Integer> values) {
            addCriterion("group_id not in", values, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdBetween(Integer value1, Integer value2) {
            addCriterion("group_id between", value1, value2, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotBetween(Integer value1, Integer value2) {
            addCriterion("group_id not between", value1, value2, "groupId");
            return (Criteria) this;
        }

        public Criteria andLocationIsNull() {
            addCriterion("location is null");
            return (Criteria) this;
        }

        public Criteria andLocationIsNotNull() {
            addCriterion("location is not null");
            return (Criteria) this;
        }

        public Criteria andLocationEqualTo(String value) {
            addCriterion("location =", value, "location");
            return (Criteria) this;
        }

        public Criteria andLocationNotEqualTo(String value) {
            addCriterion("location <>", value, "location");
            return (Criteria) this;
        }

        public Criteria andLocationGreaterThan(String value) {
            addCriterion("location >", value, "location");
            return (Criteria) this;
        }

        public Criteria andLocationGreaterThanOrEqualTo(String value) {
            addCriterion("location >=", value, "location");
            return (Criteria) this;
        }

        public Criteria andLocationLessThan(String value) {
            addCriterion("location <", value, "location");
            return (Criteria) this;
        }

        public Criteria andLocationLessThanOrEqualTo(String value) {
            addCriterion("location <=", value, "location");
            return (Criteria) this;
        }

        public Criteria andLocationLike(String value) {
            addCriterion("location like", value, "location");
            return (Criteria) this;
        }

        public Criteria andLocationNotLike(String value) {
            addCriterion("location not like", value, "location");
            return (Criteria) this;
        }

        public Criteria andLocationIn(List<String> values) {
            addCriterion("location in", values, "location");
            return (Criteria) this;
        }

        public Criteria andLocationNotIn(List<String> values) {
            addCriterion("location not in", values, "location");
            return (Criteria) this;
        }

        public Criteria andLocationBetween(String value1, String value2) {
            addCriterion("location between", value1, value2, "location");
            return (Criteria) this;
        }

        public Criteria andLocationNotBetween(String value1, String value2) {
            addCriterion("location not between", value1, value2, "location");
            return (Criteria) this;
        }

        public Criteria andRemarksIsNull() {
            addCriterion("remarks is null");
            return (Criteria) this;
        }

        public Criteria andRemarksIsNotNull() {
            addCriterion("remarks is not null");
            return (Criteria) this;
        }

        public Criteria andRemarksEqualTo(String value) {
            addCriterion("remarks =", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksNotEqualTo(String value) {
            addCriterion("remarks <>", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksGreaterThan(String value) {
            addCriterion("remarks >", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksGreaterThanOrEqualTo(String value) {
            addCriterion("remarks >=", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksLessThan(String value) {
            addCriterion("remarks <", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksLessThanOrEqualTo(String value) {
            addCriterion("remarks <=", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksLike(String value) {
            addCriterion("remarks like", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksNotLike(String value) {
            addCriterion("remarks not like", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksIn(List<String> values) {
            addCriterion("remarks in", values, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksNotIn(List<String> values) {
            addCriterion("remarks not in", values, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksBetween(String value1, String value2) {
            addCriterion("remarks between", value1, value2, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksNotBetween(String value1, String value2) {
            addCriterion("remarks not between", value1, value2, "remarks");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}