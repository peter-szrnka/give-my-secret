import { Controller, Get, Res } from '@nestjs/common';
import { AppService } from './app.service';
import { Response } from 'express';

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get("/simple")
  async simpleCredential() : Promise<string> {
    return await this.appService.getSimpleStringValue('test.jks');
  }

  @Get("/multiple")
  async multipleCredential() : Promise<string> {
    return await this.appService.getMultipleStringValues('test.p12');
  }
}
