import http from 'k6/http';
import { Rate } from 'k6/metrics';

const failureRate = new Rate('failed_requests');

export function test_api_endpoints_config() {
    const res = http.get('http://localhost:8080/books');
    failureRate.add(res.status !== 200);
}

export function test_api_create_book() {
    const res = http.post('http://localhost:8080/books',
        JSON.stringify({ title: 'Clean Code', author: 'Robert Martin' }),
        { headers: { 'Content-Type': 'application/json' } }
    );
    failureRate.add(res.status !== 201);
}