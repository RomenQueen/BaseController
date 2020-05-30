package com.rq.demo.bean;

import com.rq.ctr.net.BaseBean;

import java.io.Serializable;
import java.util.List;

public class WeatherBean extends BaseBean implements Serializable {
    /**
     * data : {"yesterday":{"date":"29日星期五","high":"高温 33℃","fx":"东风","low":"低温 17℃","fl":"<![CDATA[2级]]>","type":"小雨"},"city":"北京","forecast":[{"date":"30日星期六","high":"高温 30℃","fengli":"<![CDATA[3级]]>","low":"低温 21℃","fengxiang":"南风","type":"霾"},{"date":"31日星期天","high":"高温 29℃","fengli":"<![CDATA[3级]]>","low":"低温 17℃","fengxiang":"西北风","type":"中雨"},{"date":"1日星期一","high":"高温 32℃","fengli":"<![CDATA[2级]]>","low":"低温 17℃","fengxiang":"西南风","type":"多云"},{"date":"2日星期二","high":"高温 34℃","fengli":"<![CDATA[2级]]>","low":"低温 21℃","fengxiang":"西南风","type":"晴"},{"date":"3日星期三","high":"高温 33℃","fengli":"<![CDATA[2级]]>","low":"低温 18℃","fengxiang":"西北风","type":"晴"}],"ganmao":"感冒低发期，天气舒适，请注意多吃蔬菜水果，多喝水哦。","wendu":"28"}
     * status : 1000
     * desc : OK
     */

    private DataBean data;
    private int status;
    private String desc;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static class DataBean implements Serializable {
        /**
         * yesterday : {"date":"29日星期五","high":"高温 33℃","fx":"东风","low":"低温 17℃","fl":"<![CDATA[2级]]>","type":"小雨"}
         * city : 北京
         * forecast : [{"date":"30日星期六","high":"高温 30℃","fengli":"<![CDATA[3级]]>","low":"低温 21℃","fengxiang":"南风","type":"霾"},{"date":"31日星期天","high":"高温 29℃","fengli":"<![CDATA[3级]]>","low":"低温 17℃","fengxiang":"西北风","type":"中雨"},{"date":"1日星期一","high":"高温 32℃","fengli":"<![CDATA[2级]]>","low":"低温 17℃","fengxiang":"西南风","type":"多云"},{"date":"2日星期二","high":"高温 34℃","fengli":"<![CDATA[2级]]>","low":"低温 21℃","fengxiang":"西南风","type":"晴"},{"date":"3日星期三","high":"高温 33℃","fengli":"<![CDATA[2级]]>","low":"低温 18℃","fengxiang":"西北风","type":"晴"}]
         * ganmao : 感冒低发期，天气舒适，请注意多吃蔬菜水果，多喝水哦。
         * wendu : 28
         */

        private YesterdayBean yesterday;
        private String city;
        private String ganmao;
        private String wendu;
        private List<ForecastBean> forecast;

        public YesterdayBean getYesterday() {
            return yesterday;
        }

        public void setYesterday(YesterdayBean yesterday) {
            this.yesterday = yesterday;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getGanmao() {
            return ganmao;
        }

        public void setGanmao(String ganmao) {
            this.ganmao = ganmao;
        }

        public String getWendu() {
            return wendu;
        }

        public void setWendu(String wendu) {
            this.wendu = wendu;
        }

        public List<ForecastBean> getForecast() {
            return forecast;
        }

        public void setForecast(List<ForecastBean> forecast) {
            this.forecast = forecast;
        }

        public static class YesterdayBean implements Serializable {
            /**
             * date : 29日星期五
             * high : 高温 33℃
             * fx : 东风
             * low : 低温 17℃
             * fl : <![CDATA[2级]]>
             * type : 小雨
             */

            private String date;
            private String high;
            private String fx;
            private String low;
            private String fl;
            private String type;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getHigh() {
                return high;
            }

            public void setHigh(String high) {
                this.high = high;
            }

            public String getFx() {
                return fx;
            }

            public void setFx(String fx) {
                this.fx = fx;
            }

            public String getLow() {
                return low;
            }

            public void setLow(String low) {
                this.low = low;
            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }

        public static class ForecastBean implements Serializable {
            /**
             * date : 30日星期六
             * high : 高温 30℃
             * fengli : <![CDATA[3级]]>
             * low : 低温 21℃
             * fengxiang : 南风
             * type : 霾
             */

            private String date;
            private String high;
            private String fengli;
            private String low;
            private String fengxiang;
            private String type;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getHigh() {
                return high;
            }

            public void setHigh(String high) {
                this.high = high;
            }

            public String getFengli() {
                return fengli;
            }

            public void setFengli(String fengli) {
                this.fengli = fengli;
            }

            public String getLow() {
                return low;
            }

            public void setLow(String low) {
                this.low = low;
            }

            public String getFengxiang() {
                return fengxiang;
            }

            public void setFengxiang(String fengxiang) {
                this.fengxiang = fengxiang;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String toToast() {
                return "天气情况" +
                        "date='" + date + '\'' +
                        ", high='" + high + '\'' +
                        ", fengli='" + fengli + '\'' +
                        ", low='" + low + '\'' +
                        ", fengxiang='" + fengxiang + '\'' +
                        ", type='" + type + '\'' +
                        '}';
            }

        }
    }
}
