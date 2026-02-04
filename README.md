# TensorFlow Metal Experiments

[![Python 3.11+](https://img.shields.io/badge/python-3.11+-blue.svg)](https://www.python.org/downloads/)
[![TensorFlow 2.18+](https://img.shields.io/badge/tensorflow-2.18+-orange.svg)](https://www.tensorflow.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

Benchmarking GPU vs CPU training performance across Apple Silicon, NVIDIA GPUs, and Intel CPUs using TensorFlow Metal and MLX.

## Key Findings

![VGG16 Benchmark](assets/vgg16_benchmark.png)

**TL;DR: For large models, GPU acceleration provides 17x speedup on Apple Silicon and up to 120x on NVIDIA.**

| Hardware | GPU Cores | VGG16 (s/epoch) | Speedup vs i7-8700 |
|----------|-----------|-----------------|-------------------|
| RTX 4070 Super | 7168 CUDA | 7s | 123x |
| RTX 2070 | 2304 CUDA | 18s | 48x |
| M1 Max | 32 GPU | 21s | 41x |
| M4 Pro | 16 GPU | 26s | 33x |
| M2 | 10 GPU | 64s | 13x |
| i7-13700KF | - | 126s | 7x |
| M1 Max (CPU only) | - | 368s | 2.3x |
| i7-8700 | - | 863s | 1x (baseline) |

### Apple Silicon GPU Speedup

- **M1 Max**: 17.5x faster with Metal GPU vs CPU-only
- **M2**: 8.3x faster with Metal GPU vs CPU-only
- **M4 Pro**: See MLX vs TensorFlow comparison below

## Project Structure

```
tensorflow-metal-experiments/
├── notebooks/
│   ├── tf_mnist_train.ipynb        # Simple CNN (93k params)
│   ├── tf_fashion_mnist_train.ipynb # CNN with dropout (412k params)
│   ├── tf_cifar100-train.ipynb     # VGG16-style (34M params)
│   ├── mlx_comparison.ipynb        # MLX vs TensorFlow Metal (naive)
│   ├── optimized_benchmark.ipynb   # Naive vs Optimized comparison
│   └── benchmark_report.ipynb      # Generate benchmark charts
├── src/utils/
│   └── device_config.py            # Reusable GPU/CPU configuration
├── benchmarks/
│   └── results.json                # Structured benchmark data
└── assets/
    └── vgg16_benchmark.png         # Benchmark visualization
```

## Installation

### Prerequisites: Install Python (macOS)

If you don't have Python installed, use Homebrew:

```bash
# Install Homebrew (if not installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Python 3.11+
brew install python@3.11

# Verify installation
python3.11 --version
```

### Apple Silicon Setup (M1/M2/M3/M4)

```bash
# Navigate to project directory
cd tensorflow-metal-experiments

# Create virtual environment
python3.11 -m venv venv

# Activate virtual environment
source venv/bin/activate

# Upgrade pip
pip install --upgrade pip

# Install dependencies (TF 2.18 is required for Metal compatibility)
pip install "tensorflow>=2.18,<2.19" tensorflow-metal mlx
pip install matplotlib seaborn pandas numpy jupyterlab

# Verify TensorFlow sees the GPU
python -c "import tensorflow as tf; print(tf.config.list_physical_devices('GPU'))"
# Should show: [PhysicalDevice(name='/physical_device:GPU:0', device_type='GPU')]

# Verify MLX
python -c "import mlx.core as mx; print(mx.default_device())"
# Should show: gpu
```

### Windows with NVIDIA GPU (WSL2)

```bash
# Create and activate venv
python -m venv venv
source venv/bin/activate  # or: venv\Scripts\activate on Windows

# Install dependencies
pip install tensorflow[and-cuda]
pip install matplotlib seaborn pandas numpy jupyterlab
```

### Run Experiments

```bash
# Make sure venv is activated
source venv/bin/activate

# Start Jupyter
jupyter lab
```

Open any notebook in `notebooks/` and run all cells.

### Deactivate Environment

```bash
deactivate
```

## Switching Between GPU and CPU

Each notebook uses a device configuration helper:

```python
from utils.device_config import configure_device

# Use GPU (Metal or CUDA)
device = configure_device(use_gpu=True)

# Force CPU only
device = configure_device(use_gpu=False)
```

## Benchmarks

### VGG16 on CIFAR-100 (34M Parameters)

This is the primary benchmark. Large models show the most significant GPU acceleration.

| Hardware | Platform | GPU | Time/Epoch |
|----------|----------|-----|------------|
| RTX 4070 Super 12GB | Windows 11 | Yes | 7s |
| RTX 2070 8GB | Windows 10 | Yes | 18s |
| M1 Max 32-core GPU | macOS | Yes | 21s |
| M2 10-core GPU | macOS | Yes | 64s |
| i7-13700KF 3.4GHz | Windows 11 | No | 126s |
| M1 Max 10-core CPU | macOS | No | 368s |
| M2 8-core CPU | macOS | No | 528s |
| i9 2.4GHz 8-core | macOS | No | 630s |
| i7-8700 3.2GHz | Windows 10 | No | 863s |

### Small Model Caveat

For small models (MNIST CNN, 93k params), CPU can sometimes match or beat GPU due to data transfer overhead. GPU acceleration is most beneficial for:
- Models > 1M parameters
- Batch sizes >= 64
- Training runs with many epochs

## Performance Optimization

### Why GPU Utilization May Be Low (~40%)

If you observe low GPU utilization during training, these are the common causes:

1. **NumPy array bottleneck** - Using `model.fit(x_train, y_train)` with NumPy arrays is a major bottleneck
2. **Small batch sizes** - GPU dispatch overhead doesn't amortize for small batches
3. **Model too small** - GPU parallelism not fully utilized for models < 1M params
4. **Data loading on CPU** - Pipeline not optimized for GPU

### Optimization Tips

1. **Use tf.data.Dataset API** instead of NumPy arrays:
   ```python
   # Instead of: model.fit(x_train, y_train)
   dataset = tf.data.Dataset.from_tensor_slices((x_train, y_train))
   dataset = dataset.batch(128).prefetch(tf.data.AUTOTUNE)
   model.fit(dataset)
   ```
   This can achieve up to 5x acceleration and better GPU utilization.

2. **Increase batch sizes** - Apple's unified memory allows larger batches (try 256, 512) without CPU-GPU transfer overhead

3. **Use mixed precision** where supported:
   ```python
   tf.keras.mixed_precision.set_global_policy('mixed_float16')
   ```

4. **Monitor GPU power** to verify GPU is being utilized:
   ```bash
   sudo powermetrics --samplers gpu_power -i1000 -n1
   ```

5. **For MLX**: Use `mx.eval()` strategically to control lazy evaluation

Run `notebooks/optimized_benchmark.ipynb` to see the impact of these optimizations with real benchmarks comparing naive vs optimized implementations for both TensorFlow and MLX.

## MLX vs TensorFlow Metal

The `mlx_comparison.ipynb` notebook benchmarks Apple's MLX framework against TensorFlow Metal.

### M4 Pro Benchmark Results

Benchmarked on **M4 Pro (16-core GPU, 48GB RAM)** - Naive vs Optimized:

| Model | Params | TF Naive | TF Optimized | MLX Naive | MLX Optimized | Best |
|-------|--------|----------|--------------|-----------|---------------|------|
| MNIST CNN | 93K | 77.2s | 24.8s | 16.4s | **11.6s** | MLX Opt |
| Fashion CNN | 412K | 95.3s | 28.2s | 28.0s | **24.1s** | MLX Opt |

### Optimization Impact

| Framework | Optimization | MNIST Speedup | Fashion Speedup |
|-----------|--------------|---------------|-----------------|
| TensorFlow | tf.data + batch=256 | **3.1x faster** | **3.4x faster** |
| MLX | eval per epoch + batch=256 | 1.4x faster | 1.2x faster |

**Key Insights**:
- **TensorFlow benefits most from optimization** - tf.data.Dataset provides 3x+ speedup
- **MLX is fast out of the box** - Already optimized, less room for improvement
- **MLX wins for small/medium models** - Even optimized TensorFlow can't catch up

### When to Use Each Framework

**When to use MLX:**
- Small-to-medium models (< 10M parameters) - fastest option
- Rapid prototyping on Apple Silicon
- Apple-native applications (Core ML integration)
- When you want good performance without optimization work

**When to use TensorFlow Metal:**
- Cross-platform deployment requirements
- Access to TensorFlow Hub / Keras ecosystem
- Production pipelines with TensorFlow Serving
- When you'll invest in tf.data optimization

## Methodology

- All benchmarks run 3 times, median reported
- System was idle during benchmarks (no background tasks)
- Same model architecture across all hardware
- Data loading time excluded from measurements
- Batch sizes kept consistent (64 for MNIST, 128 for CIFAR-100)

## Contributing

1. Run benchmarks on your hardware
2. Add results to `benchmarks/results.json`
3. Run `notebooks/benchmark_report.ipynb` to regenerate charts
4. Submit a pull request

## License

MIT
