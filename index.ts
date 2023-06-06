
import { NativeModules, NativeEventEmitter, Platform } from 'react-native';
const { CarefreeWebViewTBSUtils } = NativeModules;

const eventEmitter = new NativeEventEmitter(CarefreeWebViewTBSUtils);

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

export type InitTBSReturn =
  ItemType<'downloadFinish', number> |
  ItemType<'installFinish', number> |
  ItemType<'downloadProgress', number> |
  ItemType<'viewInitFinished', boolean> |
  ItemType<'coreInitFinished', boolean>

/**
 * 获取初始x5内核加载信息
*/
export const getInitX5sdkInfo = (): InitX5sdkInfoReturn | null => {
  if (Platform.OS === "android") {
    return NativeModules.CarefreeWebViewTBSUtils.getInitX5sdkInfo();
  }
  return null
}

/**
 * 下载x5内核
*/
export const initTBS = (listener?: (info: InitTBSReturn | null) => void) => {
  if (Platform.OS === "android") {
    eventEmitter.addListener('CarefreeWebViewTBSUtils', (info) => {
      if (listener) {
        listener(info)
      }
    });
  } else if (listener) {
    listener(null)
  }
}




export default CarefreeWebViewTBSUtils;
