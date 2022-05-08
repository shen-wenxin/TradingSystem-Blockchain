import axios from "axios";
import QiniuUtil from "../api/file/qiniu";
import ResponseExtractor from "./response-extractor";

/**
 * 检查图片格式和大小是否符合要求
 * @param file 图片文件
 * @param limitedSize 限制大小(MB)
 * @returns {boolean}
 */
export function checkImageFormat(file, limitedSize) {
  const isJpgOrPng =
    file.type === "image/jpeg" || file.type === "image/png";
  if (!isJpgOrPng) {
    return false;
  }
  const isInLimitedSize = file.size / 1024 / 1024 < limitedSize;
  if (!isInLimitedSize) {
    return false;
  }
  return true;
}

/**
 * 七牛云上传普通图片（不带key）
 * @param uploadPath 存储桶上传地址
 * @param resourceUrl 对象存储外链地址
 * @param file 要上传的文件（只允许单个）
 * @returns {Promise} 上传请求结果
 */
export async function uploadImgs(uploadPath, resourceUrl, file) {
  const uploadData = new FormData();
  const tokenResponseData = await QiniuUtil.getUploadToken('');
  const token = ResponseExtractor.getData(tokenResponseData);
  uploadData.append("token", token);
  uploadData.append("file", file);
  return new Promise((resolve, reject) => {
    axios({
      url: uploadPath,
      method: 'post',
      processData: false,
      data: uploadData,
    }).then(resp => {
      const {key} = resp.data;
      const imageUrl = resourceUrl + key;
      resolve(imageUrl);
      //await UserService.updateUserAvatar(newAvatarUrl + randomVersionParam)
    }).catch(() => {
      reject();
    })
  })
}