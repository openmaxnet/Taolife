declare module 'cos-wx-sdk-v5' {
  namespace COS {
    interface Credentials {
      TmpSecretId: string
      TmpSecretKey: string
      SecurityToken: string
      StartTime?: number
      ExpiredTime?: number
    }

    interface CredentialInfo extends Credentials {}

    interface ProgressInfo {
      percent: number
      loaded: number
      total: number
      file: File
      filename: string
    }

    interface PutObjectResult {
      statusCode: number
      ETag: string
      Location: string
      VersionId?: string
      BucketVersion?: string
    }

    interface CosError extends Error {
      message: string
      code?: string
      requestId?: string
    }

    interface GetAuthorization {
      (options: unknown, callback: (credentials: Credentials) => void): void
    }

    interface PutObjectOptions {
      Bucket: string
      Region: string
      Key: string
      Body?: File | string | ArrayBuffer
      FilePath?: string
      onProgress?: (info: ProgressInfo) => void
      [key: string]: unknown
    }

    interface CosSdk {
      putObject(options: PutObjectOptions, callback: (err: CosError | null, data: PutObjectResult) => void): void
    }

    interface CosStatic {
      new (config: { getAuthorization: GetAuthorization }): CosSdk
    }
  }

  const COS: COS.CosStatic
  export default COS
}
