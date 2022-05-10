const TimeService = {
    /**
     * 将 时间戳转为日期
     * */
    timesampToTime(timestamp) {
        console.log(timestamp);
        var len = timestamp.length;
        var timenum = Number(timestamp);
        if (len === 10) {
            timenum = timenum * 1000;
        }
        let date = new Date(timenum);
        var Y = date.getFullYear() + '-';
        var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
        var D = date.getDate() + ' ';
        var h = date.getHours() + ':';
        var m = date.getMinutes() + ':';
        var s = date.getSeconds();
        return Y + M + D + h + m + s;
    },

    /**
     * 得到当前月份
     */
    nowTime() {
        let nowDate = new Date();
        let date = {
            year: nowDate.getFullYear(),
            month: nowDate.getMonth() + 1,
            date: nowDate.getDate(),
        }
        return date
    }

}

export default TimeService;