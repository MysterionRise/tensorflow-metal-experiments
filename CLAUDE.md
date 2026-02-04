# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This repository contains TensorFlow and MLX benchmarking experiments comparing GPU vs CPU performance across different hardware configurations:
- Apple Silicon (M1 Max, M2, M4 Pro) with TensorFlow Metal and MLX
- Intel CPUs
- NVIDIA GPUs (RTX 2070, RTX 4070 Super)

The experiments use Jupyter notebooks to train neural networks on standard datasets (MNIST, Fashion MNIST, CIFAR-100).

## Project Structure

```
tensorflow-metal-experiments/
├── notebooks/           # Jupyter notebooks for training experiments
├── src/utils/           # Reusable Python modules (device config)
├── benchmarks/          # Benchmark results in JSON format
└── assets/              # Generated charts and images
```

## Environment Setup

### Apple Silicon (M1/M2/M3/M4) with GPU support
```bash
# Install Python if needed: brew install python@3.11
python3.11 -m venv venv
source venv/bin/activate
pip install tensorflow tensorflow-metal mlx
pip install matplotlib seaborn pandas jupyterlab
```

### Windows with NVIDIA GPU (WSL2)
```bash
python -m venv venv
source venv/bin/activate
pip install tensorflow[and-cuda]
pip install matplotlib seaborn pandas jupyterlab
```

## Running Experiments

```bash
jupyter lab
```

Then open and run any notebook in `notebooks/`.

## Key Commands

```bash
# Verify TensorFlow GPU
python -c "import tensorflow as tf; print(tf.config.list_physical_devices('GPU'))"

# Verify MLX
python -c "import mlx.core as mx; print(mx.default_device())"

# Generate benchmark charts
jupyter execute notebooks/benchmark_report.ipynb
```

## Notebook Structure

Each training notebook follows this pattern:
1. **Device configuration cell**: Uses `src/utils/device_config.py` to select GPU or CPU
2. **Model definition cell**: Builds a Keras Sequential model with list syntax
3. **Training cell**: Loads dataset, trains model, reports timing

To switch between GPU and CPU:
```python
from utils.device_config import configure_device
device = configure_device(use_gpu=True)   # GPU
device = configure_device(use_gpu=False)  # CPU only
```

## Key Files

- `notebooks/tf_mnist_train.ipynb` - Simple CNN on MNIST (~93k params)
- `notebooks/tf_fashion_mnist_train.ipynb` - CNN with dropout on Fashion MNIST (~412k params)
- `notebooks/tf_cifar100-train.ipynb` - VGG16-style network on CIFAR-100 (~34M params)
- `notebooks/mlx_comparison.ipynb` - MLX vs TensorFlow Metal comparison (naive implementations)
- `notebooks/optimized_benchmark.ipynb` - Naive vs Optimized benchmark (tf.data, mx.compile)
- `notebooks/benchmark_report.ipynb` - Generates benchmark charts from results.json
- `benchmarks/results.json` - Structured benchmark data
- `src/utils/device_config.py` - Reusable GPU/CPU configuration helper

## Adding New Benchmarks

1. Run experiments and record results
2. Add data to `benchmarks/results.json`
3. Run `notebooks/benchmark_report.ipynb` to regenerate charts
4. Charts are saved to `assets/`
