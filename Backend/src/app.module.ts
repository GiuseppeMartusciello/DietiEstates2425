import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { configValidationSchema } from './config.schema';
import { AuthModule } from './auth/auth.module';
import { AgencyModule } from './agency/agency.module';
import { AgentModule } from './agent/agent.module';
import { ListingModule } from './listing/listing.module';
import { AgencyManagerModule } from './agency-manager/agency-manager.module';
import { NotificationModule } from './notification/notification.module';
import { PropertyOfferModule } from './property_offer/property_offer.module';
import { SupportAdminModule } from './support-admin/support-admin.module';
import { ClientModule } from './client/client.module';
import { ResearchModule } from './research/research.module';
import { GeoapifyService } from './common/services/geopify.service';
import { AdminModule } from './admin/admin.module';

@Module({ 
  providers: [GeoapifyService],
  exports: [GeoapifyService],
  imports: [
    ConfigModule.forRoot({
      envFilePath: ['.env'],
      validationSchema: configValidationSchema,
    }),
    TypeOrmModule.forRootAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: async (configService: ConfigService) => ({
        type: 'postgres',
        autoLoadEntities: true,
        synchronize: true,
        host: configService.get('DB_HOST'),
        port: configService.get('DB_PORT'),
        username: configService.get('DB_USERNAME'),
        password: configService.get('DB_PASSWORD'),
        database: configService.get('DB_DATABASE'),
        //logging: 'all',
      }),
    }),
    AuthModule,
    AgencyModule,
    AgentModule,
    ListingModule,
    AgencyManagerModule,
    NotificationModule,
    PropertyOfferModule,
    ResearchModule,
    SupportAdminModule,
    ClientModule,
    AdminModule,
  ],
})
export class AppModule {}
