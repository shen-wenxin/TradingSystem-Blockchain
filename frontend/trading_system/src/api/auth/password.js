import HttpService from "../../config/axios";
import CryptoJS from 'crypto-js'

const PasswordService = {
    /**
     * 对传入的明文密码进行哈希
     * @param password 明文密码
     * @param random 随机串
     * @returns 哈希后的字符串
     */
    encryptPassword(password, random) {
        return CryptoJS.SHA256(password + random).toString();
    },

    /**
     * 修改密码的第一步认证
     * @returns {AxiosPromise}
     */
    preAuthentication() {
        return HttpService({
            url: '/changePassword_verifyId',
            method: 'post'
        })
    },

    /**
     * 修改密码的第二步认证
     * @param encryptedOldPassword
     * @returns {AxiosPromise}
     */
    verifyOldPassword(encryptedOldPassword) {
        return HttpService({
            url: '/changePassword_verifyPassword',
            method: 'post',
            data: {
                password: encryptedOldPassword
            }
        })
    },
}

export default PasswordService;

