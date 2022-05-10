<template>
  <div>
    <v-dialog v-model="showUploadDialog" persistent max-width="600">
      <template v-slot:activator="{ on, attrs }">
        <v-btn
            text
            color="primary"
            class="mx-4"
            dark
            v-bind="attrs"
            v-on="on"
        >
          上传 / 修改
        </v-btn>
      </template>
      <v-card>
        <v-card-title class="headline">选择文件并上传您的头像</v-card-title>
        <v-card-text class="mx-auto">
          <v-row class="my-1" justify="center">
            <v-col cols="auto">
              <a-upload
                  v-if="!hasChosenImage"
                  name="file"
                  :file-list="fileList"
                  list-type="picture-card"
                  class="avatar-uploader"
                  :show-upload-list="false"
                  :before-upload="beforeUpload"
              >
                <div class="uploadBtn">
                  <a-icon type="plus"/>
                  <div class="ant-upload-text">
                    Upload
                  </div>
                </div>
              </a-upload>
            </v-col>
          </v-row>


          <div id="cropper-wrapper" v-if="isCropping">
            <VueCropper
                ref="cropper"
                :img="imageCropOptions.img"
                :outputSize="imageCropOptions.size"
                :outputType="imageCropOptions.outputType"
                :autoCrop="true"
                :fixed="true"
                :fixedNumber="[1, 1]"
                :centerBox="true"
            ></VueCropper>
          </div>


        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="success" text @click="confirmAvatarCrop" :disabled="!isCropping">
            确认
          </v-btn>
          <v-btn color="error" text @click="closeUploadDialog">取消</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </div>
</template>


<script>
import {VueCropper} from 'vue-cropper'
import axios from 'axios';
import QiniuUtil from "../api/file/qiniu";
import ResponseExtractor from "../utils/response-extractor";
import UserService from "../api/auth/user";

export default {
  name: "AvatarUpload",
  components: {
    VueCropper,
  },
  data() {
    return {
      hasChosenImage: false,
      showUploadDialog: false,
      isCropping: false,
      fileList: [],
      uploadPath: 'http://upload-z2.qiniup.com',
      imageCropOptions: {
        img: '',
        size: 1,
        outputType: 'jpeg'
      },
    }
  },
  methods: {
    initUploadDialog() {
      this.hasChosenImage = false;
      this.isCropping = false;
      this.fileList = [];
      this.imageCropOptions.img = '';
    },
    closeUploadDialog() {
      this.showUploadDialog = false;
      this.initUploadDialog();
    },
    checkImageFormat(file) {
      // 图片类型检测，若非jpg或png，驳回
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isJpgOrPng) {
        this.$message.error('You can only upload JPG or PNG file!');
        return false;
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error('Image must smaller than 2MB!');
        return false;
      }
      return true;
    },

    showChosenImageToCrop(file) {
      let fr = new FileReader();
      fr.readAsDataURL(file)
      fr.onload = () => {
        this.imageCropOptions.img = fr.result;
        this.isCropping = true;
      }
    },

    /* 钩子，文件上传之前做的事情 */
    beforeUpload(file) {

      // 检查图片格式和大小
      if (!this.checkImageFormat(file)) {
        return false;
      }

      this.hasChosenImage = true;

      //console.log(file); //for debug

      // 把选择的图片读到裁剪框中展示，下一步让用户进行裁剪
      this.showChosenImageToCrop(file);

      // 拦截组件的自动上传，自己实现手动上传
      return false;
    },

    // 裁剪之后点击确认，上传裁剪好的图片
    async confirmAvatarCrop() {

      const uploadData = new FormData();

      // 拉取用户信息，生成即将上传的头像文件在七牛云中的key/
      await this.$store.dispatch('user/getInfo');
      const avatarKey = 'avatar_' + this.$store.state.user.account;
      uploadData.append('key', avatarKey);

      // 生成七牛云上传策略（已废弃，勿删，暂时保留）
      // let policy = {};
      // const bucketName = 'hitszoj';
      // const deadline = Math.round(new Date().getTime() / 1000) + 3600;
      // policy.scope = bucketName + ':' + avatarKey; // 将scope设置为<bucket>:<key>才能覆盖原文件
      // policy.deadline = deadline;

      // 生成七牛云上传token
      const tokenResponseData = await QiniuUtil.getUploadToken(avatarKey);
      const token = ResponseExtractor.getData(tokenResponseData);

      // 将token附带到请求的formData中
      uploadData.append('token', token);

      // 获取裁剪后的图片，放入待上传文件列表
      this.$refs.cropper.getCropBlob(data => {

        this.fileList.push(data);
        uploadData.append('file', this.fileList[0]);

        // 发送上传请求
        axios({
          url: this.uploadPath,
          method: 'post',
          processData: false,
          data: uploadData,
        }).then((resp) => {
          const {key} = resp.data;
          const newAvatarUrl = 'http://euphonium.cn/' + key;
          const randomVersionParam = '?v=' + Math.round(new Date().getTime() / 1000);
          console.log('avatarUrl', newAvatarUrl + randomVersionParam);
          UserService.updateUserAvatar(newAvatarUrl + randomVersionParam).then(() => {
            this.closeUploadDialog();
            this.$message.success('上传成功');
            this.$emit('onUploadSuccess');
          })
        }).catch(() => {
          this.closeUploadDialog();
          this.$message.error('上传失败，请重新尝试上传');
          this.$emit('onUploadFailed');
        })
      })

    },


  }
}
</script>

<style scoped>
.uploadBtn {
  width: 350px;
}

.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}

#cropper-wrapper {
  height: 300px;
  width: auto;
}
</style>