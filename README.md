# AdTech AI SRE Platform

## Overview

This project simulates real-time production incidents in AdTech platforms, including:

- Real-Time Bidding (RTB)
- DSP bid response failures
- SSP downstream timeout issues
- Dynamic Ad Insertion failures during live events

The goal is to build an AI-powered SRE system that can detect incidents, analyze metrics, identify probable root cause, and recommend recovery actions.

## Project Goals

- Simulate RTB and live ad insertion production issues
- Monitor services using Prometheus and Grafana
- Use Kubernetes HPA for scaling
- Build AI-assisted RCA and troubleshooting recommendations
- Reduce MTTR using observability and automation

## Initial Architecture

```text
SSP Simulator
      ↓
RTB Auction Service
      ↓
DSP Bidder Service
      ↓
DAI Service
      ↓
AI SRE Agent