import { Injectable, InternalServerErrorException } from '@nestjs/common';
import axios from 'axios';

@Injectable()
export class GeoapifyService {
  private readonly apiKey = '719a27ab3b2a4f76a0f68bd33ad7f510';
  private readonly baseUrl = 'https://api.geoapify.com/v1/geocode/search';

  async getCoordinatesFromAddress(address: string): Promise<{ lat: number; lon: number }> {
    try {
      const response = await axios.get(this.baseUrl, {
        params: {
          text: address,
          apiKey: this.apiKey,
        },
      });

      const features = response.data.features;
      if (features.length === 0) {
        throw new Error('No results from Geoapify');
      }

      const { lat, lon } = features[0].properties;
      return { lat, lon };
    } catch (error) {
      console.error('[GeoapifyService] Error:', error.message);
      throw new InternalServerErrorException('Failed to get coordinates from Geoapify');
    }
  }

  async getNearbyIndicators(lat: number, lon: number): Promise<string[]> {
    try {
      const categories = [
        'education.school',         // scuole
        'leisure.park',             // parchi
        'public_transport.stop',    // trasporti pubblici
      ];
      console.log('[GeoapifyService] Query:', {
        categories: categories.join(','),
        filter: `circle:${lon},${lat},500`,
        apiKey: this.apiKey,
      });
      const response = await axios.get('https://api.geoapify.com/v2/places', {
        params: {
          categories: categories.join(','),
          filter: `circle:${lon},${lat},500`, // raggio 500m
          limit: 20,
          apiKey: this.apiKey,
        },
      });
  
      const features = response.data.features;
      const foundCategories = new Set<string>();
  
      for (const feature of features) {
        const cat = feature.properties.categories || [];
        if (cat.includes('education')) foundCategories.add('Vicino a scuole');
        if (cat.includes('leisure')) foundCategories.add('Vicino a parchi');
        if (cat.includes('public_transport')) foundCategories.add('Vicino a trasporto pubblico');
      }
  
      return Array.from(foundCategories);
    } catch (error) {
      console.error('[GeoapifyService][POI] Error:', error.message);
      return [];
    }
  }
  
}
