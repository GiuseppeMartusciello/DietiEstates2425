import * as admin from 'firebase-admin';
import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class PushNotificationService {
  constructor(private readonly config: ConfigService) {
    if (!admin.apps.length) {
      admin.initializeApp({
        credential: admin.credential.cert({
          projectId: this.config.get<string>('FIREBASE_PROJECT_ID')!,
          clientEmail: this.config.get<string>('FIREBASE_CLIENT_EMAIL')!,
          privateKey: this.config
            .get<string>('FIREBASE_PRIVATE_KEY')!
            .replace(/\\n/g, '\n'),
        }),
      });
    }
  }

  async sentToDevice(token: string, title: string, body: string) {
    return await admin.messaging().send({
      token: token,
      notification: {
        title: title,
        body: body,
      },
    });
  }
}
