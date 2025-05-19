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
      const normalizedLat = Number(lat).toFixed(6);
      const normalizedLon = Number(lon).toFixed(6);
  
      const response = await axios.get('https://api.geoapify.com/v2/places', {
        params: {
          categories: 'education.school,leisure.park,public_transport.bus',
          filter: `circle:${normalizedLon},${normalizedLat},500`,
          limit: 8,
          apiKey: this.apiKey,
        },
      });
  
      const features = response.data.features;
      const minDistances: Record<string, number> = {};
      
      for (const feature of features) {
        const cat = feature.properties.categories || [];
        const featureLat = feature.properties.lat;
        const featureLon = feature.properties.lon;

        const distance = this.calculateDistance(lat, lon, featureLat, featureLon);

        if (cat.includes('education'))
          minDistances['scuole'] = Math.min(minDistances['scuole'] ?? Infinity, distance);
          
        if (cat.includes('leisure'))         
          minDistances['parchi'] = Math.min(minDistances['parchi'] ?? Infinity, distance);

        if (cat.includes('public_transport'))
          minDistances['trasporti'] = Math.min(minDistances['trasporti'] ?? Infinity, distance);
        
      }
  
      return Object.entries(minDistances).map(
        ([label, distance]) => `${label}:${Math.round(distance)}`
      );
    } catch (error) {
      console.error('[GeoapifyService][POI] Error:', error.message);
      return [];
    }
  }


   calculateDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const R = 6371; // Raggio della Terra in km
    const dLat = (lat2 - lat1) * (Math.PI / 180);
    const dLon = (lon2 - lon1) * (Math.PI / 180);
  
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1 * (Math.PI / 180)) *
        Math.cos(lat2 * (Math.PI / 180)) *
        Math.sin(dLon / 2) *
        Math.sin(dLon / 2);
  
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
     const distanzaKm= R * c; // distanza in km
     return Math.round(distanzaKm * 1000);
  }
  
  
  
}
