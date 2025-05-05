import {
  applyDecorators,
  UseInterceptors,
} from '@nestjs/common';
import {
  FilesInterceptor,
} from '@nestjs/platform-express';
import { diskStorage } from 'multer';
import * as fs from 'fs';
import * as path from 'path';

export function ListingImageUploadInterceptor(fieldName = 'images', maxCount = 10) {
  return applyDecorators(
    UseInterceptors(
      FilesInterceptor(fieldName, maxCount, {
        storage: diskStorage({
          destination: (req, file, cb) => {
            const listingId = req.params.id; // ⚠️ Funziona solo se chiamato correttamente
            const uploadPath = path.join('uploads', listingId || 'undefined');

            fs.mkdirSync(uploadPath, { recursive: true });
            cb(null, uploadPath);
          },
          filename: (req, file, cb) => {
            const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1e9);
            cb(null, `${uniqueSuffix}-${file.originalname}`);
          },
        }),
      }),
    ),
  );
}
