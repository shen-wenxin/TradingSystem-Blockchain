<template>
  <v-btn 
    depressed
    :disabled="loading"
    class="ma-1"
    color="black"
    @click = getFileUrl()
    plain>
    点击下载已上传的文件
  </v-btn>

</template>
<script>
import {getDownloadToken } from '@/api/file/data2backend.js';
// import {getFileFromQiniuyun} from '@/api/qiniu.js'
// import axios from "axios";
  export default {
    props:["homework"],
    data () {
      return {
        loading: false,
        token: '',

      }
    },
    methods:{
      async remove () {
        this.loading = true
        await new Promise(resolve => setTimeout(resolve, 3000))
        this.loading = false
      },

      getFileUrl(){
        //获取学生学号，此处采用假学号,请后期修改
        let account = "19011";
        // 获取该课题的名字
        let title = this.homework.title;

        let data = {
          account: account,
          title: title
        }


        //将信息封装好给后端，索取tokon
        getDownloadToken(data).then(res =>{
          console.log("---------要下载链接--------:", res.data)
          this.token = res.data.data;
          //开始下载
          let link = document.createElement('a')
          link.style.display = 'none'
          link.href = this.token
          // 生成时间戳
          let timestamp = new Date().getTime()
          link.download = timestamp + '.txt'
          document.body.appendChild(link)
          link.click()

          //   axios({
          //   url: this.token,
          //   method: 'get',
          //  })
          //   .then(res =>{
          //     console.log("get here")

          //     console.log(res.data)
          //     let blob = new Blob([res.data])
          //     console.log(blob)

          //     let fileName = this.homework.title + '_' + account  + ".zip"
          //     if(window.navigator.msSaveOrOpenBlob){
          //       navigator.msSaveBlob(blob, fileName)
          //     }
          //     else{
          //       var link = document.createElement('a')
          //       link.href = window.URL.createObjectURL(blob);
          //       link.download = fileName
          //       link.click()
          //       window.URL.revokeObjectURL(link.href)
          //     }
          //   }
          //   )
          //   .catch(error =>{
          //     console.log(error)
          //   })
        })

        // let test = "http://qzqinqe09.hn-bkt.clouddn.com/code.txt?e=1634136314&token=s-PX_pO4g6XxGc2IFT9NX7ZjMlk5cxgFQQZye65E:EeNoGPtomW5R_eD04L4ELruaFXk="

        //得到token 之后向七牛云发送请求
        

      }

    },

  }
</script>