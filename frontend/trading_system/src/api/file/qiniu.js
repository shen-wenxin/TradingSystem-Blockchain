import HttpService from "../../config/axios";

const QiniuUtil = {
  getUploadToken(uploadFileKey) {
    return HttpService({
      url: 'files/getToken', // 假地址 自行替换
      method: 'get',
      params: {
        uploadFileKey
      }
    })
  },
}

export default QiniuUtil;
