// 将对响应体信息的提取方式单独解耦出来
const ResponseExtractor = {
    /**
     * 取出服务端自定义响应码
     */
    getCode(response) {
        return response.data.code;
    },

    /**
     * 取出服务端自定义响应信息
     */
    getMsg(response) {
        return response.data.msg;
    },

    /**
     * 取出服务端自定义响应体中的数据部分
     */
    getData(response) {
        return response.data.data;
    },

    /**
     * 取出服务端自定义响应体中的响应成功标识
     */
    isSuccessful(response) {
        return response.data.success;
    }
}

export default ResponseExtractor;