import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { Logger, ValidationPipe } from '@nestjs/common';
import { TransformInterceptor } from './common/interceptors/transform.interceptor';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  const logger = new Logger();

  app.enableCors({
    origin: 'http://localhost:5173', // Permetti le richieste da Vite
    credentials: true, // Obbligatorio se usi cookie o header di auth
  });

  app.useGlobalPipes(
    new ValidationPipe({
      transform: true, // Trasforma i payload in DTO automaticamente
      whitelist: true, // Rimuove proprietà non dichiarate nei DTO
      forbidNonWhitelisted: true, // (opzionale) Lancia errore se ci sono proprietà extra
    }),
  );
  app.useGlobalInterceptors(new TransformInterceptor());
  await app.listen(process.env.PORT ?? 3000);
  logger.log(`Application listening on port ${process.env.PORT ?? 3000}`);
}
bootstrap();
