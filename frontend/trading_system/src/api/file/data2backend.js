import HttpService from '../../config/axios'
//该文件用于前端向后端发送数据


export function sendFiledata2Backend(data) {
  return HttpService({
    //url 接口，请根据实际修改网址
    url: 'files/uploadFile',
    method: 'post',
    data

  })
}

export function getDownloadToken(data){
  return HttpService({
    url: "files/downloadFileByAccount", //假地址
    method: 'post',
    data
  })
}