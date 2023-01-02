import { Controller, Get, Res } from '@nestjs/common';
import { AppService } from './app.service';
import { Response } from 'express';

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get()
  async getHello(/*@Res() res: Response*/) : Promise<string> {
    return await this.appService.getHello();/*.then(response => {
      console.info("response on controller", response);
      res.json({ success : true });
    });*/
  }
}
