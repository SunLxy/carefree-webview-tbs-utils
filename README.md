
下载x5内核工具包

## 安装

```bash
$ npm install carefree-webview-tbs-utils --save
```

## 使用

### 获取初始x5内核加载信息(android)

```ts
  try{
    const result = await getInitX5sdkInfo()
    console.log(result)
  }catch(err){
    console.log(err)
  }

```
### 下载x5内核(android)

注意：当`内核下载完成`之后，返回`x5内核加载失败`时,需要重新启动应用

```ts
  try{
    initDownloadTBS((info)=>{
      console.log(info)
    })
  }catch(err){
    console.log(err)
  }

```

## 类型

```ts

export interface InitX5sdkInfoReturn {
  isX5DownloadFinish: number,// 下载X5内核完成
  isX5InstallFinish: number,// 安装X5内核进度
  isX5DownloadProgress: number,// 下载过程的通知，提供当前下载进度[0-100]
  isX5ViewInitFinished: boolean,// x5內核初始化完成的回调，true表x5内核加载成功，否则表加载失败，会自动切换到系统内核。
  isX5CoreInitFinished: boolean,// 内核初始化完毕
  isDisableX5: boolean,// 是否禁用 x5内核
}

interface ItemType<T, P> {
  /**
   * 类型
   * */
  type: T;
  /**
   * errorCode 或进度条
   * */
  errorCode: P;
  /**
   * 类型文字描述
  */
  msg: string
}

export type InitDownloadTBSReturn =
  ItemType<'downloadFinish', number> |// 下载结束时的状态，下载成功时errorCode为100,其他均为失败，外部不需要关注具体的失败原因
  ItemType<'installFinish', number> |// 安装结束时的状态，安装成功时errorCode为200,其他均为失败，外部不需要关注具体的失败原因
  ItemType<'downloadProgress', number> |// 下载过程的通知，提供当前下载进度[0-100] ，其他值参考[https://x5.tencent.com/docs/tbsapi/reference/com/tencent/smtt/sdk/TbsCommonCode.html]
  ItemType<'viewInitFinished', boolean> |// x5內核初始化完成的回调，true表x5内核加载成功，否则表加载失败，会自动切换到系统内核。
  ItemType<'coreInitFinished', boolean>// 内核初始化完毕 都是直接返回true,只是为了说明走了这个方法

/**
 * 获取初始x5内核加载信息
*/
export const getInitX5sdkInfo = async (): Promise<InitX5sdkInfoReturn | null> => void

/**
 * 下载x5内核
*/
export const initDownloadTBS = (listener?: (info: InitDownloadTBSReturn | null) => void) => void

```
